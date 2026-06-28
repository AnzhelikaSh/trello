import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { fetchBoardById, clearCurrentBoard } from '../../store/boardSlice';
import {
    DndContext,
    closestCorners,
    DragOverlay,
    defaultDropAnimationSideEffects,
} from '@dnd-kit/core';
import {
    SortableContext,
    verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import {
    Container,
    Box,
    Typography,
    Button,
    IconButton,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    CircularProgress,
    Paper,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import AddIcon from '@mui/icons-material/Add';
import Column from './Column';
import api from '../../services/api';

function BoardDetail() {
    const { boardId } = useParams();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { currentBoard, isLoading } = useSelector((state) => state.boards);
    const [openColumnDialog, setOpenColumnDialog] = useState(false);
    const [newColumnTitle, setNewColumnTitle] = useState('');
    const [columns, setColumns] = useState([]);
    const [activeTask, setActiveTask] = useState(null);

    useEffect(() => {
        if (boardId) {
            dispatch(fetchBoardById(boardId));
        }
        return () => {
            dispatch(clearCurrentBoard());
        };
    }, [dispatch, boardId]);

    // обновляем локальный список колонок
    useEffect(() => {
        if (currentBoard) {
            // Если колонки есть — используем их, если нет — создаём пустой массив
            const boardColumns = currentBoard.columns || [];

            // Убеждаемся, что у каждой колонки есть tasks (даже если null)
            const columnsWithTasks = boardColumns.map(col => ({
                ...col,
                tasks: col.tasks || []
            }));

            setColumns(columnsWithTasks);
        }
    }, [currentBoard]);

    // --- Создание колонки ---
    const handleCreateColumn = async () => {
        if (!newColumnTitle.trim()) {
            alert('Введите название колонки');
            return;
        }

        console.log('📤 СОЗДАНИЕ КОЛОНКИ: начато');
        console.log('boardId:', boardId);
        console.log('title:', newColumnTitle);

        try {
            const response = await api.post(`/boards/${boardId}/columns`, {
                title: newColumnTitle
            });

            console.log('✅ КОЛОНКА СОЗДАНА:', response.data);

            setNewColumnTitle('');
            setOpenColumnDialog(false);

            // Обновляем доску, чтобы получить обновлённый список колонок
            await dispatch(fetchBoardById(boardId));

        } catch (error) {
            console.error('❌ ОШИБКА:', error);
            alert('Ошибка: ' + (error.response?.data?.message || error.message));
        }
    };

    // --- Создание задачи ---
    const handleCreateTask = async (columnId, taskData) => {
        try {
            await api.post(`/boards/${boardId}/columns/${columnId}/tasks`, taskData);
            dispatch(fetchBoardById(boardId));
        } catch (error) {
            console.error('Ошибка при создании задачи:', error);
            alert('Ошибка при создании задачи');
        }
    };

    // --- Редактирование задачи ---
    const handleUpdateTask = async (task) => {
        const newTitle = prompt('Новое название задачи:', task.title);
        if (newTitle && newTitle !== task.title) {
            try {
                await api.put(`/boards/${boardId}/columns/1/tasks/${task.id}`, {
                    ...task,
                    title: newTitle,
                });
                dispatch(fetchBoardById(boardId));
            } catch (error) {
                console.error('Ошибка при обновлении задачи:', error);
                alert('Ошибка при обновлении задачи');
            }
        }
    };

    // --- Удаление задачи ---
    const handleDeleteTask = async (taskId) => {
        if (window.confirm('Удалить задачу?')) {
            try {
                await api.delete(`/boards/${boardId}/columns/1/tasks/${taskId}`);
                dispatch(fetchBoardById(boardId));
            } catch (error) {
                console.error('Ошибка при удалении задачи:', error);
                alert('Ошибка при удалении задачи');
            }
        }
    };

    // --- Обновление колонки ---
    const handleUpdateColumn = async (columnId, columnData) => {
        try {
            await api.put(`/boards/${boardId}/columns/${columnId}`, columnData);
            dispatch(fetchBoardById(boardId));
        } catch (error) {
            console.error('Ошибка при обновлении колонки:', error);
            alert('Ошибка при обновлении колонки');
        }
    };

    // --- Удаление колонки ---
    const handleDeleteColumn = async (columnId) => {
        if (window.confirm('Удалить колонку? Все задачи в ней тоже будут удалены!')) {
            try {
                await api.delete(`/boards/${boardId}/columns/${columnId}`);
                dispatch(fetchBoardById(boardId));
            } catch (error) {
                console.error('Ошибка при удалении колонки:', error);
                alert('Ошибка при удалении колонки');
            }
        }
    };

    // --- Drag-and-drop ---
    const handleDragStart = (event) => {
        const { active } = event;
        const taskId = active.id;

        for (const column of columns) {
            const task = column.tasks?.find(t => t.id === taskId);
            if (task) {
                setActiveTask(task);
                break;
            }
        }
    };

    const handleDragEnd = async (event) => {
        const { active, over } = event;
        setActiveTask(null);

        if (!over) return;

        const taskId = active.id;
        const destinationColumnId = parseInt(over.id);

        if (isNaN(destinationColumnId)) return;

        let sourceColumnId = null;
        let task = null;
        for (const column of columns) {
            const foundTask = column.tasks?.find(t => t.id === taskId);
            if (foundTask) {
                sourceColumnId = column.id;
                task = foundTask;
                break;
            }
        }

        if (!task || sourceColumnId === destinationColumnId) return;

        try {
            await api.patch(`/boards/${boardId}/columns/1/tasks/${taskId}/position`, {
                destinationColumnId: destinationColumnId,
                newPosition: 0
            });

            dispatch(fetchBoardById(boardId));
        } catch (error) {
            console.error('Ошибка при перемещении задачи:', error);
            alert('Ошибка при перемещении задачи');
        }
    };

    const dropAnimation = {
        sideEffects: defaultDropAnimationSideEffects({
            styles: {
                active: {
                    opacity: '0.4',
                },
            },
        }),
    };

    if (isLoading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    if (!currentBoard) {
        return (
            <Container>
                <Typography>Доска не найдена</Typography>
                <Button onClick={() => navigate('/boards')}>Вернуться к доскам</Button>
            </Container>
        );
    }

    return (
        <Container maxWidth={false} sx={{ px: 3, py: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <IconButton onClick={() => navigate('/boards')} sx={{ mr: 2 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h4" component="h1">
                    {currentBoard.title}
                </Typography>
                <Box sx={{ flex: 1 }} />
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={() => setOpenColumnDialog(true)}
                >
                    Добавить колонку
                </Button>
            </Box>

            {currentBoard.description && (
                <Typography color="textSecondary" sx={{ mb: 3 }}>
                    {currentBoard.description}
                </Typography>
            )}

            <DndContext
                collisionDetection={closestCorners}
                onDragStart={handleDragStart}
                onDragEnd={handleDragEnd}
            >
                <Box sx={{ display: 'flex', overflowX: 'auto', pb: 2 }}>
                    {columns.map((column) => (
                        <div key={column.id}>
                            <SortableContext
                                items={column.tasks?.map(task => task.id) || []}
                                strategy={verticalListSortingStrategy}
                            >
                                <Column
                                    column={column}
                                    boardId={boardId}
                                    onCreateTask={handleCreateTask}
                                    onUpdateTask={handleUpdateTask}
                                    onDeleteTask={handleDeleteTask}
                                    onUpdateColumn={handleUpdateColumn}
                                    onDeleteColumn={handleDeleteColumn}
                                />
                            </SortableContext>
                        </div>
                    ))}
                </Box>
                <DragOverlay dropAnimation={dropAnimation}>
                    {activeTask ? (
                        <Paper sx={{ p: 1, bgcolor: 'white', boxShadow: 6, cursor: 'grabbing' }}>
                            <Typography>{activeTask.title}</Typography>
                        </Paper>
                    ) : null}
                </DragOverlay>
            </DndContext>

            <Dialog open={openColumnDialog} onClose={() => setOpenColumnDialog(false)}>
                <DialogTitle>Добавить колонку</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Название колонки"
                        fullWidth
                        value={newColumnTitle}
                        onChange={(e) => setNewColumnTitle(e.target.value)}
                        onKeyPress={(e) => e.key === 'Enter' && handleCreateColumn()}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenColumnDialog(false)}>Отмена</Button>
                    <Button onClick={handleCreateColumn} variant="contained">
                        Добавить
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
}

export default BoardDetail;