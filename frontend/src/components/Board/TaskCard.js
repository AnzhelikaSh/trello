import React from 'react';
import { Paper, Typography, Box, IconButton } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { styled } from '@mui/material/styles';

const TaskPaper = styled(Paper)(({ theme }) => ({
    padding: theme.spacing(1.5),
    marginBottom: theme.spacing(1),
    cursor: 'pointer',
    '&:hover': {
        backgroundColor: theme.palette.grey[50],
        boxShadow: theme.shadows[2],
    },
}));

function TaskCard({ task, onEdit, onDelete }) {
    return (
        <TaskPaper elevation={1}>
            <Box display="flex" justifyContent="space-between" alignItems="flex-start">
                <Typography variant="body1" sx={{ flex: 1, wordBreak: 'break-word' }}>
                    {task.title}
                </Typography>
                <Box>
                    <IconButton size="small" onClick={(e) => { e.stopPropagation(); onEdit(task); }}>
                        <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton size="small" onClick={(e) => { e.stopPropagation(); onDelete(task.id); }}>
                        <DeleteIcon fontSize="small" />
                    </IconButton>
                </Box>
            </Box>
            {task.description && (
                <Typography variant="caption" color="textSecondary" sx={{ mt: 0.5, display: 'block' }}>
                    {task.description}
                </Typography>
            )}
        </TaskPaper>
    );
}

export default TaskCard;