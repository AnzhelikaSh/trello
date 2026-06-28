import React, { useState } from 'react';
import { Paper, Typography, Box, Button, TextField, IconButton } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { styled } from '@mui/material/styles';

const ColumnPaper = styled(Paper)(({ theme }) => ({
    width: 280,
    minWidth: 280,
    backgroundColor: '#f4f5f7',
    padding: theme.spacing(1),
    marginRight: theme.spacing(2),
    display: 'flex',
    flexDirection: 'column',
    maxHeight: 'calc(100vh - 200px)',
    overflow: 'hidden',
}));

const TasksContainer = styled(Box)({
    flex: 1,
    overflowY: 'auto',
    minHeight: 100,
    padding: '4px',
});

// Компонент для одной задачи с поддержкой перетаскивания
function SortableTask({ task, onEdit, onDelete }) {
    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
        isDragging,
    } = useSortable({ id: task.id });

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
        opacity: isDragging ? 0.5 : 1,
        padding: '8px 12px',
        marginBottom: '8px',
        backgroundColor: 'white',
        borderRadius: '4px',
        boxShadow: '0 1px 3px rgba(0,0,0,0.12)',
        cursor: 'grab',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        '&:hover': {
            backgroundColor: '#f5f5f5',
        },
    };

    return (
        <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
            <Typography variant="body2" sx={{ flex: 1, mr: 1, wordBreak: 'break-word' }}>
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
        </div>
    );
}

function Column({ column, boardId, onCreateTask, onUpdateTask, onDeleteTask, onUpdateColumn, onDeleteColumn }) {
    const [isAddingTask, setIsAddingTask] = useState(false);
    const [newTaskTitle, setNewTaskTitle] = useState('');
    const [isEditingTitle, setIsEditingTitle] = useState(false);
    const [editTitle, setEditTitle] = useState(column.title);

    const handleAddTask = () => {
        if (newTaskTitle.trim()) {
            onCreateTask(column.id, { title: newTaskTitle, description: '' });
            setNewTaskTitle('');
            setIsAddingTask(false);
        }
    };

    const handleUpdateTitle = () => {
        if (editTitle.trim() && editTitle !== column.title) {
            onUpdateColumn(column.id, { title: editTitle });
        }
        setIsEditingTitle(false);
    };

    return (
        <ColumnPaper elevation={2}>
            <Box display="flex" justifyContent="space-between" alignItems="center" p={1}>
                {isEditingTitle ? (
                    <TextField
                        size="small"
                        value={editTitle}
                        onChange={(e) => setEditTitle(e.target.value)}
                        onBlur={handleUpdateTitle}
                        onKeyPress={(e) => e.key === 'Enter' && handleUpdateTitle()}
                        autoFocus
                    />
                ) : (
                    <Typography
                        variant="h6"
                        sx={{ cursor: 'pointer', fontWeight: 'bold', display: 'flex', alignItems: 'center' }}
                        onClick={() => setIsEditingTitle(true)}
                    >
                        {column.title}
                        <EditIcon fontSize="small" sx={{ ml: 0.5, opacity: 0.5 }} />
                    </Typography>
                )}
                <IconButton size="small" onClick={() => onDeleteColumn(column.id)}>
                    <DeleteIcon fontSize="small" />
                </IconButton>
            </Box>

            <TasksContainer>
                {column.tasks?.map((task) => (
                    <SortableTask
                        key={task.id}
                        task={task}
                        onEdit={onUpdateTask}
                        onDelete={onDeleteTask}
                    />
                ))}
                {(!column.tasks || column.tasks.length === 0) && (
                    <Typography variant="body2" color="textSecondary" sx={{ p: 1, textAlign: 'center' }}>
                        Нет задач
                    </Typography>
                )}
            </TasksContainer>

            {isAddingTask ? (
                <Box p={1}>
                    <TextField
                        fullWidth
                        size="small"
                        placeholder="Название задачи..."
                        value={newTaskTitle}
                        onChange={(e) => setNewTaskTitle(e.target.value)}
                        onKeyPress={(e) => e.key === 'Enter' && handleAddTask()}
                        autoFocus
                    />
                    <Box display="flex" gap={1} mt={1}>
                        <Button size="small" variant="contained" onClick={handleAddTask}>
                            Добавить
                        </Button>
                        <Button size="small" onClick={() => setIsAddingTask(false)}>
                            Отмена
                        </Button>
                    </Box>
                </Box>
            ) : (
                <Button
                    startIcon={<AddIcon />}
                    onClick={() => setIsAddingTask(true)}
                    sx={{ justifyContent: 'flex-start', m: 1 }}
                >
                    Добавить задачу
                </Button>
            )}
        </ColumnPaper>
    );
}

export default Column;