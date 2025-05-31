<template>
    <div>
        <transition name="fades">
            <LoadingTransition v-if="showLoading" />
        </transition>
        <Navbar :username="username" />
        <div id="app-content">
            <router-view v-slot="{ Component }">
                <transition name="fade" mode="out-in">
                    <component :is="Component" />
                </transition>
            </router-view>
        </div>
        <Footer v-once />
    </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from "vue";
import Navbar from "@/components/Navbar.vue";
import Footer from "@/components/Footer.vue";

export default {
    components: {
        Navbar,
        Footer,
    },
    data() {
        return {
            username: null,
            showLoading: true,
        };
    },
    mounted() {
        this.loadUsername();
    },
    methods: {
        loadUsername() {
            const username = localStorage.getItem("username");
            if (username) {
                this.username = username;
            }
        },
        checkContent() {
            const appContent = document.getElementById('app-content');
            this.showLoading = !appContent || appContent.querySelectorAll('div').length === 0;
        },
    },
    watch: {
        '$route': 'checkContent',
        username(newUsername) {
            localStorage.setItem('username', newUsername);
        },
    },
    created() {
        let interval = setInterval(this.checkContent, 1000);
        onBeforeUnmount(() => clearInterval(interval));
    },
};
</script>
<style>
a {
    text-decoration: none;
}

li {
    list-style: none;
}

body {
    background-color: var(--bs-light);
    color: black;
    font-family: Arial, Helvetica, sans-serif;
}

#app-content {
    min-height: 100vh;
}

.fade-enter-active,
.fade-leave-active {
    transition: all 0.5s;
}

.fade-enter,
.fade-leave-to {
    opacity: 0;
}

.fade-enter-from {
    transform: translateY(10vh);
}

.fade-leave-to {
    transform: translateY(-100vh);
}

.fades-enter-active,
.fades-leave-active {
    transition: opacity 0.5s;
}

.fades-enter,
.fades-leave-to {
    opacity: 0;
}
</style>
