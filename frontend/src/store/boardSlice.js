import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import api from '../services/api';

// Получить все доски пользователя
export const fetchBoards = createAsyncThunk(
    'boards/fetchBoards',
    async () => {
        const response = await api.get('/boards');
        return response.data;
    }
);

// Создать новую доску
export const createBoard = createAsyncThunk(
    'boards/createBoard',
    async (boardData) => {
        const response = await api.post('/boards', boardData);
        return response.data;
    }
);

// Получить одну доску по ID (для страницы с колонками)
export const fetchBoardById = createAsyncThunk(
    'boards/fetchBoardById',
    async (boardId) => {
        const response = await api.get(`/boards/${boardId}`);
        return response.data;
    }
);

// Обновить доску
export const updateBoard = createAsyncThunk(
    'boards/updateBoard',
    async ({ boardId, boardData }) => {
        const response = await api.put(`/boards/${boardId}`, boardData);
        return response.data;
    }
);

// Удалить доску
export const deleteBoard = createAsyncThunk(
    'boards/deleteBoard',
    async (boardId) => {
        await api.delete(`/boards/${boardId}`);
        return boardId;
    }
);

const boardSlice = createSlice({
    name: 'boards',
    initialState: {
        list: [],           // Список всех досок
        currentBoard: null, // Текущая открытая доска
        isLoading: false,
        error: null,
    },
    reducers: {
        clearCurrentBoard: (state) => {
            state.currentBoard = null;
        },
    },
    extraReducers: (builder) => {
        builder
            // Получение всех досок
            .addCase(fetchBoards.pending, (state) => {
                state.isLoading = true;
            })
            .addCase(fetchBoards.fulfilled, (state, action) => {
                state.isLoading = false;
                state.list = action.payload;
            })
            .addCase(fetchBoards.rejected, (state, action) => {
                state.isLoading = false;
                state.error = action.error.message;
            })
            // Создание доски
            .addCase(createBoard.fulfilled, (state, action) => {
                state.list.push(action.payload);
            })
            // Получение одной доски
            .addCase(fetchBoardById.pending, (state) => {
                state.isLoading = true;
            })
            .addCase(fetchBoardById.fulfilled, (state, action) => {
                state.isLoading = false;
                state.currentBoard = action.payload;
            })
            .addCase(fetchBoardById.rejected, (state, action) => {
                state.isLoading = false;
                state.error = action.error.message;
            })
            // Удаление доски
            .addCase(deleteBoard.fulfilled, (state, action) => {
                state.list = state.list.filter(board => board.id !== action.payload);
            });
    },
});

export const { clearCurrentBoard } = boardSlice.actions;
export default boardSlice.reducer;