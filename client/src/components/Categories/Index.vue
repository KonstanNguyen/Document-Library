<template>
	<div class="container d-flex justify-content-between pt-5">
        <Block v-for="item in ListCategories"
        :key="item.id"
        :iconName="item.iconName"
        :name="item.name"
        :categoryId="item.id"/>
    </div>
</template>

<script>
    import apiClient from "@/api/service";
	import Block from './Block.vue';
	export default {
        components: { Block },
		data() {
			return {
                ListCategories: [
                    {
                        iconName: 'book',
                        categorySlug: 'giao-duc',
                    },
                    {
                        iconName: 'light',
                        categorySlug: 'cong-nghe',
                    },
                    {
                        iconName: 'chess',
                        categorySlug: 'khoa-hoc',
                    },
                    {
                        iconName: 'sale',
                        categorySlug: 'kinh-te',
                    },
                    {
                        iconName: 'hand',
                        categorySlug: 'suc-khoe',
                    },
                    {
                        iconName: 'fly',
                        categorySlug: 'tu-dong-hoa',
                    },
                    {
                        iconName: 'money',
                        categorySlug: 'marketing',
                    },
                ],
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
                    console.log(this.ListCategories);
                } catch (error) {
                    console.error('Error fetching categories:', error);
                }
            }
        }
    }
</script>

<style lang="scss" scoped>

</style>