<template>
    <div class="m-auto mt-5" style="width: 90%;">
        <table class="table table-striped table-hover">
            <thead class="thead-dark">
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Tiêu đề</th>
                    <th scope="col" style="width: 20%;">Người dùng đã tải</th>
                    <th scope="col">Thời gian tải</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in listHistoryDownload" :key="item.id">
                    <th scope="row">{{ index + 1 }}</th>
                    <td>{{ item.documentName }}</td>
                    <td>{{ item.username }}</td>
                    <td>{{ formatDate(item.date) }}</td>
                </tr>
            </tbody>
        </table>
        <Pagination />
    </div>
</template>

<script>
import { computed } from "vue";
import apiClient from "@/api/service";
import Pagination from "../BlogEtc/Pagination.vue";

export default {
    components: { Pagination },
    data() {
        return {
            listHistoryDownload: [],
            page: {
                current: 1,
                max: 1,
            },
        }
    },
    methods: {
        async fetchData() {
            const paginationRequest = {
                page: this.page.current,
                size: 6,
                sortBy: "id",
                sortDirection: "asc",
            };

            try {
                const response = await apiClient.get('api/history-downloads', { params: paginationRequest });
                const data = response.data;
                this.listHistoryDownload = data;
                this.page.max = data.totalPages;
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        },
        formatDate(date) {
            const d = new Date(date);
            return d.toLocaleString();
        },
        async gotoPage(page) {
            if (page >= 1 && page <= this.page.max) {
                this.page.current = page;
                await this.fetchData();
                this.scrollToTop();
            }
        },
        async gotoPrePage() {
            if (this.page.current > 1) {
                this.page.current--;
                await this.fetchData();
                this.scrollToTop();
            }
        },

        async gotoNxtPage() {
            if (this.page.current < this.page.max) {
                this.page.current++;
                await this.fetchData();
                this.scrollToTop();
            }
        },
        scrollToTop() {
            window.scrollTo({ top: 0, behavior: "smooth" });
        },
    },
    provide() {
        return {
            gotoPage: this.gotoPage,
            gotoPrePage: this.gotoPrePage,
            gotoNxtPage: this.gotoNxtPage,
            page: computed(() => this.page),
        };
    },
    mounted() {
        this.fetchData();
    }
};
</script>
<style scoped lang="scss">
.table {
    width: 100%;
    margin-bottom: 1rem;
    background-color: #fff;
}

.table th,
.table td {
    text-align: center;
    vertical-align: middle;
}

.table th {
    background-color: #343a40;
    color: #fff;
}

.table tbody tr:hover {
    background-color: #f1f1f1;
}

.badge {
    font-size: 14px;
    padding: 0.4rem 0.8rem;
    color: #343a40;
}

.btn-sm {
    font-size: 14px;
    padding: 0.375rem 0.75rem;
}

.btn i {
    margin-right: 5px;
}

.btn-primary {
    background-color: #007bff;
    border-color: #007bff;
}

.btn-danger {
    background-color: #dc3545;
    border-color: #dc3545;
}
</style>
