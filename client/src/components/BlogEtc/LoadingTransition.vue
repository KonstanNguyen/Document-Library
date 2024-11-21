<template>
	<div
		class="loading-transition position-absolute bg-white top-0 start-0"
		style="width: 100vw; height: 100vh; z-index: 10000">
		<div
			class="top-50 start-50 translate-middle position-absolute"
			style="width: fit-content">
			<div
				class="spinner-border text-primary"
				role="status">
				<span class="visually-hidden">Loading...</span>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
		name: 'LoadingTrans',
	};
</script>

<style lang="scss"></style>
