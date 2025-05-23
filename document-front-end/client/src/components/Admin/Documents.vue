<template>
    <div class="container d-flex justify-content-around pt-5" style="width: 30%;">
        <Block v-for="item in Options" :link="item.link" :name="item.name" @click="fetchDocuments(item.link)" />
    </div>
    <div class="m-auto mt-5" style="width: 90%;">
        <table class="table table-striped table-hover" v-if="selectedLink === 'request'">
            <thead class="thead-dark">
                <tr>
                    <th scope="col">#</th>
                    <th scope="col" style="width: 30%;">Tiêu đề</th>
                    <th scope="col">Danh mục</th>
                    <th scope="col">Thời gian gửi</th>
                    <th scope="col">Người đăng</th>
                    <th scope="col" style="width: 22%;">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in listDocRequest">
                    <th scope="row">{{ index + 1 }}</th>
                    <td>{{ item.title }}</td>
                    <td>{{ item.categoryName }}</td>
                    <td>{{ item.createAt }}</td>
                    <td>{{ item.authorName }}</td>

                    <td class="d-flex gap-2">
                        <button class="btn btn-primary btn-sm"
                        @click="approveDocument(item.id)">
                            <i class="bi bi-pencil-square"></i> Duyệt
                        </button>
                        <button class="btn btn-danger btn-sm"><i class="bi bi-trash3-fill"></i> Từ chối</button>
                        <button class="btn btn-primary btn-sm"><i class="bi bi-eye"></i> Xem </button>
                    </td>
                </tr>
            </tbody>
        </table>
        <table class="table table-striped table-hover" v-if="selectedLink === 'all'">
            <thead class="thead-dark">
                <tr>
                    <th scope="col">#</th>
                    <th scope="col" style="width: 30%;">Tiêu đề</th>
                    <th scope="col">Danh mục</th>
                    <th scope="col">Thời gian cập nhật</th>
                    <th scope="col">Người đăng</th>
                    <th scope="col">Lượt xem</th>
                    <th scope="col">Đánh giá</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in listDocAll">
                    <th scope="row">{{ index + 1 }}</th>
                    <td>{{ item.title }}</td>
                    <td>{{ item.categoryName }}</td>
                    <td>{{ item.updateAt }}</td>
                    <td>{{ item.authorName }}</td>
                    <td>{{ item.views }}</td>
                    <td>{{ item.ratingAvg }}</td>
                </tr>
            </tbody>
        </table>
        <Pagination />
    </div>
</template>

<script>
import { computed } from "vue";
import Block from './OptionBlock.vue';
import apiClient from "@/api/service";
import Pagination from "../BlogEtc/Pagination.vue";

export default {
    components: { Block, Pagination },
    data() {
        return {
            listDocRequest: [],
            listDocAll: [],
            selectedLink: "all",
            Options: [
                {
                    name: "Tất cả tài liệu",
                    link: "all",
                    path: "/admin/documents/",
                },
                {
                    name: "Tài liệu cần duyệt",
                    link: "request",
                    path: "/admin/documents/",
                },
            ],
            page: {
                current: 1,
                max: 1,
            },
        }
    },
    methods: {
        async fetchDocuments(link) {
            this.selectedLink = link;
            if (link === "request") {
                this.fetchData();
            } else if (link === "all") {
                this.fetchAllData();
            }
        },
        async fetchData() {
            const paginationRequest = {
                page: this.page.current - 1,
                size: 9,
                sortBy: "createAt",
                sortDirection: "desc",
                status: 0,
            };

            try {
                const response = await apiClient.get('/api/documents', { params: paginationRequest });
                const data = response.data;

                if (data && data.content) {
                    this.listDocRequest = data.content;
                    this.page.max = data.totalPages;
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        },
        async fetchAllData() {
            const paginationRequest = {
                page: this.page.current - 1,
                size: 9,
                sortBy: "createAt",
                sortDirection: "desc",
                status: 1,
            };

            try {
                const response = await apiClient.get('/api/documents', { params: paginationRequest });
                const data = response.data;

                if (data && data.content) {
                    this.listDocAll = data.content;
                    this.page.max = data.totalPages;
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        },
        async approveDocument(documentId) {
            try {
                const updatedDocument = { status: 1 };
                console.log("Sending request:", updatedDocument);
                const response = await apiClient.put(`/api/documents/${documentId}/update`, updatedDocument);
                console.log("Response from server:", response.data);
                this.fetchDocuments(this.selectedLink);
                alert("Tài liệu đã được duyệt!");
            } catch (error) {
                console.error('Error approving document:', error);
                alert("Có lỗi xảy ra khi duyệt tài liệu!");
            }
        },
        formatDate(date) {
            const d = new Date(date);
            const day = String(d.getDate()).padStart(2, '0');
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const year = d.getFullYear();
            return `${day}/${month}/${year}`;
        },
        async gotoPage(page) {
            if (page >= 1 && page <= this.page.max) {
                this.page.current = page;
                if (this.selectedLink === "request") {
                    await this.fetchData();
                } else if (this.selectedLink === "all") {
                    await this.fetchAllData();
                }
                this.scrollToTop();
            }
        },
        async gotoPrePage() {
            if (this.page.current > 1) {
                this.page.current--;
                if (this.selectedLink === "request") {
                    await this.fetchData();
                } else if (this.selectedLink === "all") {
                    await this.fetchAllData();
                }
                this.scrollToTop();
            }
        },

        async gotoNxtPage() {
            if (this.page.current < this.page.max) {
                this.page.current++;
                if (this.selectedLink === "request") {
                    await this.fetchData();
                } else if (this.selectedLink === "all") {
                    await this.fetchAllData();
                }
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
    created() {
        this.fetchDocuments("all");
    },
    mounted() {
        this.fetchDocuments("all");
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
