import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchBoards, createBoard, deleteBoard } from '../../store/boardSlice';
import { useNavigate } from 'react-router-dom';
import {
    Container,
    Grid,
    Card,
    CardContent,
    Typography,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    TextField,
    DialogActions,
    Box,
    IconButton,
    CardActions,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import { styled } from '@mui/material/styles';

const StyledCard = styled(Card)(({ theme }) => ({
    cursor: 'pointer',
    transition: 'transform 0.2s, box-shadow 0.2s',
    '&:hover': {
        transform: 'translateY(-4px)',
        boxShadow: theme.shadows[8],
    },
}));

function BoardList() {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { list, isLoading } = useSelector((state) => state.boards);
    const { user } = useSelector((state) => state.auth);
    const [openDialog, setOpenDialog] = useState(false);
    const [newBoard, setNewBoard] = useState({ title: '', description: '' });

    useEffect(() => {
        dispatch(fetchBoards());
    }, [dispatch]);

    const handleCreateBoard = async () => {
        if (newBoard.title.trim()) {
            await dispatch(createBoard(newBoard));
            setOpenDialog(false);
            setNewBoard({ title: '', description: '' });
            dispatch(fetchBoards());
        }
    };

    const handleDeleteBoard = async (e, boardId) => {
        e.stopPropagation();
        if (window.confirm('Вы уверены, что хотите удалить эту доску?')) {
            await dispatch(deleteBoard(boardId));
        }
    };

    if (isLoading && list.length === 0) {
        return (
            <Container>
                <Typography align="center" sx={{ mt: 8 }}>
                    Загрузка досок...
                </Typography>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
                <Typography variant="h4" component="h1">
                    Мои доски
                </Typography>
                {user && (
                    <Typography variant="subtitle1" color="textSecondary">
                        {user.fullName || user.username}
                    </Typography>
                )}
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => setOpenDialog(true)}
                >
                    Создать доску
                </Button>
            </Box>

            {list.length === 0 ? (
                <Box textAlign="center" sx={{ mt: 8 }}>
                    <Typography variant="h6" color="textSecondary" gutterBottom>
                        У вас пока нет досок
                    </Typography>
                    <Button
                        variant="outlined"
                        startIcon={<AddIcon />}
                        onClick={() => setOpenDialog(true)}
                        sx={{ mt: 2 }}
                    >
                        Создать первую доску
                    </Button>
                </Box>
            ) : (
                <Grid container spacing={3}>
                    {list.map((board) => (
                        <Grid item xs={12} sm={6} md={4} key={board.id}>
                            <StyledCard onClick={() => navigate(`/boards/${board.id}`)}>
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>
                                        {board.title}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        {board.description || 'Нет описания'}
                                    </Typography>
                                </CardContent>
                                <CardActions>
                                    <IconButton
                                        size="small"
                                        color="error"
                                        onClick={(e) => handleDeleteBoard(e, board.id)}
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                </CardActions>
                            </StyledCard>
                        </Grid>
                    ))}
                </Grid>
            )}

            {/* Диалог создания доски */}
            <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>Создать новую доску</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Название доски"
                        type="text"
                        fullWidth
                        variant="outlined"
                        value={newBoard.title}
                        onChange={(e) => setNewBoard({ ...newBoard, title: e.target.value })}
                        required
                    />
                    <TextField
                        margin="dense"
                        label="Описание (необязательно)"
                        type="text"
                        fullWidth
                        multiline
                        rows={3}
                        variant="outlined"
                        value={newBoard.description}
                        onChange={(e) => setNewBoard({ ...newBoard, description: e.target.value })}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDialog(false)}>Отмена</Button>
                    <Button onClick={handleCreateBoard} variant="contained" disabled={!newBoard.title.trim()}>
                        Создать
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
}

export default BoardList;