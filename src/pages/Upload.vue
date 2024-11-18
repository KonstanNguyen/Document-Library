<script setup>
	import { ref } from 'vue';
	import Form from '@/components/ApplyCourse/Form.vue';

	const courses = ref([
		{
			id: '1',
			name: 'Phương pháp luận sáng tạo và đổi mới (TRIZ mở rộng) 1',
		},
		{
			id: '2',
			name: 'Phương pháp luận sáng tạo và đổi mới (TRIZ mở rộng) 2',
		},
	]);
</script>

<template>
	<div class="form-section pt-5 pb-5">
		<div class="container">
			<h1 class="title text-center">
				Đăng ký tham dự các chương trình của
				<br />
				<span class="focus">TSK-CSTC</span>
			</h1>
			<Form :Courses="courses" />
		</div>
	</div>
</template>

<style scoped lang="scss">
	.form-section {
		background-color: #fff;
	}
	.title {
		font-weight: 600;
	}
	.focus {
		padding: 10px 30px;
		background-color: #424242;
		color: #fff;
		line-height: 2;
	}
</style>
