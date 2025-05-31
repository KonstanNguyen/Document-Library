<script setup>
	import { defineProps, ref, onMounted, watch } from 'vue';
	const props = defineProps({
		iconUrl: {
			type: String,
			required: true,
		},
		width: {
			type: Number,
			default: null,
		},
		height: {
			type: Number,
			default: null,
		},
	});

	const iconPath = ref(null);

	const loadIcon = async (iconName) => {
		try {
			const icon = await import(`@/assets/icons/${iconName}.svg`);
			iconPath.value = icon.default; // Assign the icon URL to iconPath
		} catch (error) {
			console.error('Failed to load icon:', error);
			iconPath.value = null;
		}
	};

	onMounted(() => {
		loadIcon(props.iconUrl); // Load icon when component is mounted
	});

	// Watch for changes to iconUrl and reload the icon
	watch(
		() => props.iconUrl,
		(newIconUrl) => {
			loadIcon(newIconUrl);
		}
	);
</script>

<template>
	<div>
		<img
			:src="iconPath"
			:width="width"
			:height="height"
			alt="Icon" />
	</div>
</template>