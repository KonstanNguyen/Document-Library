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

<template>
	<div>
		<transition name="fades">
			<LoadingTransition v-if="showLoading" />
		</transition>
		<Navbar v-once />
		<div id="app-content">
			<router-view v-slot="{ Component }">
				<transition
					name="fade"
					mode="out-in">
					<component :is="Component" />
				</transition>
			</router-view>
		</div>
		<Footer v-once />
	</div>
</template>

<script setup>
	import { ref, onMounted, onBeforeUnmount } from 'vue';
	import Navbar from '@/components/Navbar.vue';
	import Footer from '@/components/Footer.vue';

	const showLoading = ref(true);

	const checkContent = () => {
		const appContent = document.getElementById('app-content');
		if (appContent && appContent.querySelectorAll('div').length === 0) {
			showLoading.value = true;
		} else {
			showLoading.value = false;
		}
	};

	let interval;
	onMounted(() => {
		checkContent();
		interval = setInterval(checkContent, 1000);
	});

	onBeforeUnmount(() => {
		clearInterval(interval);
	});
</script>
