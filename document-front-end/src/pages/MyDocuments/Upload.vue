<script setup>
	import Form from '@/components/MyDocuments/Upload/Form.vue';
</script>

<template>
	<div class ="container">
        <div class="page-title">
			<div class="d-flex">
				<h1 class="col-md-12 text-center">Upload tài liệu</h1>
			</div>
		</div>
		<Form />
	</div>
</template>

<style scoped lang="scss">
.page-title {
	padding: 50px 0;
	width: 100%;
	margin: auto;
	div {
		justify-content: center;
		h1 {
			border-bottom: 1px solid #bcbcbc;
			padding-bottom: 30px;
		}
	}
}
</style>
