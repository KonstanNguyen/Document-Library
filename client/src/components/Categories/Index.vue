<template>
    <div class="d-flex justify-content-between pt-2">
        <Block v-for="item in ListCategories" :key="item.id" :iconName="item.iconName" :name="item.name"
            :categoryId="item.id" />
    </div>
</template>

<script>
import apiClient from "@/api/service";
import Block from './Block.vue';
export default {
    components: { Block },
    data() {
        return {
            ListCategories: [],
            Icons: [
                {
                    name: "book",
                },
                {
                    name: "hand",
                },
                {
                    name: "money",
                },
                {
                    name: "book-2",
                },
                {
                    name: "fly",
                },
                {
                    name: "calendar",
                },
                {
                    name: "light",
                },
                {
                    name: "timer",
                },
            ]
        };
    },
    mounted() {
        this.fetchCategories();
    },
    methods: {
        async fetchCategories() {
            try {
                const response = await apiClient.get('/category');
                const categories = response.data;

                this.ListCategories = categories.map((category, index) => ({
                    ...category,
                    iconName: this.Icons[index % this.Icons.length].name,
                }));
            } catch (error) {
                console.error('Error fetching categories:', error);
            }
        }
    }
}
</script>

<style lang="scss" scoped></style>