import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import BoardList from './components/Board/BoardList';
import BoardDetail from './components/Board/BoardDetail';  // ← добавить
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { logout } from './store/authSlice';

function PrivateRoute({ children }) {
    const { token } = useSelector((state) => state.auth);
    return token ? children : <Navigate to="/login" />;
}

function Layout({ children }) {
    const dispatch = useDispatch();
    const { user } = useSelector((state) => state.auth);

    return (
        <>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" sx={{ flexGrow: 1, cursor: 'pointer' }} onClick={() => window.location.href = '/'}>
                        Trello Clone
                    </Typography>
                    {user && (
                        <>
                            <Typography sx={{ mr: 2 }}>
                                {user.fullName || user.username}
                            </Typography>
                            <Button color="inherit" onClick={() => dispatch(logout())}>
                                Выйти
                            </Button>
                        </>
                    )}
                </Toolbar>
            </AppBar>
            <Box sx={{ p: 3 }}>{children}</Box>
        </>
    );
}

function App() {
    return (
        <BrowserRouter>
            <Layout>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route
                        path="/boards"
                        element={
                            <PrivateRoute>
                                <BoardList />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/boards/:boardId"
                        element={
                            <PrivateRoute>
                                <BoardDetail />
                            </PrivateRoute>
                        }
                    />
                    <Route path="/" element={<Navigate to="/boards" />} />
                </Routes>
            </Layout>
        </BrowserRouter>
    );
}

export default App;
