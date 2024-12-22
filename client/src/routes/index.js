import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('@/pages/Home.vue')
        },
        {
            path: '/documents',
            name: 'Documents',
            children: [
                {
                    path: "",
                    name: 'Documents page',
                    component: () => import('@/pages/Documents/List.vue')
                },
                {
                    path: ":id",
                    name: 'Documents post',
                    component: () => import('@/pages/Documents/Post.vue')
                },
            ]
        },
        {
            path: '/my-documents',
            name: 'my documents',
            children: [
                {
                    path: ":link",
                    name: 'Documents List',
                    component: () => import('@/pages/MyDocuments/List.vue')
                },
                {
                    path: "upload",
                    name: 'Documents Upload',
                    component: () => import('@/pages/MyDocuments/Upload.vue')
                },
            ]
        },
        {
            path: '/login',
            name: 'Login',
            component: () => import('@/components/Auth/Login.vue')
        },
        {
            path: '/register',
            name: 'Register',
            component: () => import('@/components/Auth/Register.vue')
        },
        {
            path: '/category',
            name: 'Category',
            children: [
                {
                    path: ":id",
                    name: 'Documents by category',
                    component: () => import('@/pages/Category.vue')
                },
            ]
        },
        {
            path: '/admin',
            name: 'Admin',
            children: [
                {
                    path: "documents/:link",
                    name: 'Admin Documents',
                    component: () => import('@/components/Admin/Documents.vue')
                },
                {
                    path: "accounts",
                    name: 'Admin Accounts',
                    component: () => import('@/components/Admin/Accounts.vue')
                },
                {
                    path: "history-download",
                    name: 'Admin History Downloads',
                    component: () => import('@/components/Admin/HistoryDownload.vue')
                },
            ]
        },
    ],
    scrollBehavior(to, from, savedPosition) {
        if (to.hash) {
            return {
                el: to.hash,
                behavior: 'smooth',
            };
        }
        return { top: 0 };
    }
})

export default router
