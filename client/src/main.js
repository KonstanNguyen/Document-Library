import { createApp } from 'vue'
import App from './App.vue';
import Router from '@/routes';
import vietnamese from "@/assets/locales/vi-vn.json"
import english from "@/assets/locales/en-us.json"
import '@/assets/scss/Root.scss';
import AOS from 'aos';
import 'aos/dist/aos.css';

import { LoadingTransition } from '@/components/globalComponents.js'

if (localStorage.getItem("language") == null) {
    localStorage.setItem("language", "vi-vn");
}

const app = createApp(App)

// Router
app.use(Router)

// Setup language
app.config.globalProperties.vietnamese = vietnamese
app.config.globalProperties.english = english
app.config.globalProperties.language = localStorage.getItem("language")
if (localStorage.getItem("language") == "vi-vn") {
    app.config.globalProperties.langPack = vietnamese;
} else if (localStorage.getItem("language") == "en-us") {
    app.config.globalProperties.langPack = english;
}

// Setup global component
app.component('LoadingTransition', LoadingTransition);
app.mount('#app');
AOS.init();