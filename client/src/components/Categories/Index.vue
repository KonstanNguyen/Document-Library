<template>
	<div class="container d-flex justify-content-between pt-5">
        <Block v-for="item in ListCategories"
        :iconName="item.iconName"
        :name="item.name"/>
    </div>
</template>

<script>
	import Block from './Block.vue';
	export default {
        components: { Block },
		data() {
			return {
                ListCategories: [
                    {
                        iconName: 'book',
                        name: 'Giáo dục',
                    },
                    {
                        iconName: 'light',
                        name: 'Công nghệ',
                    },
                    {
                        iconName: 'chess',
                        name: 'Khoa học',
                    },
                    {
                        iconName: 'sale',
                        name: 'Kinh tế',
                    },
                    {
                        iconName: 'hand',
                        name: 'Sức khỏe',
                    },
                    {
                        iconName: 'fly',
                        name: 'Tự động hóa',
                    },
                    {
                        iconName: 'money',
                        name: 'Marketing',
                    },
                ],
            };
		},
    }
</script>

<style lang="scss" scoped>

</style>