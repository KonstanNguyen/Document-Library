<template>
	<div class="container d-flex justify-content-between pt-5">
        <Block v-for="item in ListCategories"
        :key="item.id"
        :iconName="item.iconName"
        :name="item.name"
        :categorySlug="item.categorySlug"/>
    </div>
</template>

<script>
    import apiClient from "@/api/service";
	import Block from './Block.vue';
	export default {
        components: { Block },
		data() {
			return {
                // ListCategories: [
                //     {
                //         iconName: 'book',
                //         name: 'Giáo dục',
                //         categorySlug: 'giao-duc',
                //     },
                //     {
                //         iconName: 'light',
                //         name: 'Công nghệ',
                //         categorySlug: 'cong-nghe',
                //     },
                //     {
                //         iconName: 'chess',
                //         name: 'Khoa học',
                //         categorySlug: 'khoa-hoc',
                //     },
                //     {
                //         iconName: 'sale',
                //         name: 'Kinh tế',
                //         categorySlug: 'kinh-te',
                //     },
                //     {
                //         iconName: 'hand',
                //         name: 'Sức khỏe',
                //         categorySlug: 'suc-khoe',
                //     },
                //     {
                //         iconName: 'fly',
                //         name: 'Tự động hóa',
                //         categorySlug: 'tu-dong-hoa',
                //     },
                //     {
                //         iconName: 'money',
                //         name: 'Marketing',
                //         categorySlug: 'marketing',
                //     },
                // ],
                ListCategories: [],
            };
		},
        mounted() {
            this.fetchCategories();
        },
        methods: {
            async fetchCategories() {
                try {
                    const response = await apiClient.get('/category');
                    this.ListCategories = response.data; 
                } catch (error) {
                    console.error('Error fetching categories:', error);
                }
            }
        }
    }
</script>

<style lang="scss" scoped>

</style>