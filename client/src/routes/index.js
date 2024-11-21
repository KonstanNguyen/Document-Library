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
                    path: ":slug",
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
                    path: "",
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
