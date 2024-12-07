<template>
	<router-link :to="`/admin/documents/${link}`">
		<div class="container-category" :class="{ active: isActive }">
			<div class="name">{{ name }}</div>
		</div>
	</router-link>
</template>

<script setup>
import { defineProps, computed } from "vue";
import { useRoute } from 'vue-router';
const props = defineProps({
	name: {
		type: String,
		required: true,
	},
	link: {
        type: Number,
        default: '',
    },
});

const route = useRoute();
const isActive = computed(() => route.params.link === String(props.link));
</script>

<style scoped lang="scss">
.container-category {
	display: inline-flex;
	flex-direction: row;
	width: auto;
	height: 56px;
	background-color:#ccd0d3;
	box-shadow: 0px 2px 4px -2px #0000001a;
	border-radius: 35px;
	align-items: center;
	justify-content: center;
	padding-left: 10px;
	padding-right: 12px;
	gap: 6px;
	cursor: pointer;

	&.active {
		background: #1976d2;
		.name{
			color: #fff;
		}
	}

	.logo {
		width: 40px;
		height: 30px;
		@media (max-width: 550px) {
			width: 24px;
			height: 18px;
		}
	}

	.name {
		font-family: var(--primary-font);
		font-size: 24px;
		font-weight: 600;
		line-height: 19.2px;
		color: #5c5612;

		@media (max-width: 550px) {
			font-size: 16px;
		}
	}
}

@media (max-width: 550px) {
	.container-category {
		width: auto;
		height: 40px;
	}
}
</style>