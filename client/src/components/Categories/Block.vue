<template>
	<div class="container-category" :class="{ active: isActive }"  @click="handleClick">
		<IconLoader class="right" :iconUrl="iconName" width="35" height="35"/>

		<div class="name">{{ name }}</div>
	</div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import IconLoader from '@/components/IconLoader.vue';
const props = defineProps({
	iconName: {
		type: String,
		required: true,
	},
	name: {
		type: String,
		required: true,
	},
	isActive: {
		type: Boolean,
		default: false,
	},
});
const emit = defineEmits(["click"]);

const handleClick = () => {
  emit("click", props.name);
};

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
	padding-right: 10px;
	gap: 10px;
	cursor: pointer;

	&.active {
		background: #FBFAED;

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