<script>
import apiClient from "@/api/service";
import Block from '@/components/Admin/OptionBlock.vue';
export default {
    components: { Block },
    data() {
        return {
            listDocuments: [],
            listHistoryDownload: [],
            selectedLink: "documents",
            accountId: localStorage.getItem("userId") || "",
            message: "",
            Options: [
                {
                    name: "Tài liệu của tôi",
                    link: "documents",
                },
                {
                    name: "Tài liệu đã tải",
                    link: "history-download"
                },
            ],
        };
    },
    mounted() {
        if (this.accountId) {
            this.fetchDocUserId(this.accountId);
            this.fetchHistoryDownload(this.accountId);
        } else {
            this.message = "Không tìm thấy thông tin người dùng!";
        }
    },
    methods: {
        async fetchData(link) {
            this.selectedLink = link;
            if (link === "documents") {
                this.fetchDocuments();
            } else if (link === "history-download") {
                this.fetchHistoryDownload(this.accountId);
            }
        },
        async fetchDocUserId(accountId) {
            try {
                const response = await apiClient.get(`/api/accounts/${accountId}`);
                const userId = response.data.userId;
                if (userId) {
                    this.fetchDocuments(userId);
                } else {
                    this.message = "Không tìm thấy userId!";
                }
            } catch (error) {
                console.error("Lỗi khi gọi API lấy thông tin tài khoản:", error);
                this.message = "Không thể tải thông tin người dùng.";
            }
        },
        async fetchDocuments(userId) {
            try {
                const response = await apiClient.get(`/api/doc-users/${userId}/documents`);
                this.listDocuments = response.data.content;
                if (this.listDocuments.length === 0) {
                    this.message = "Bạn chưa upload tài liệu nào!";
                } else {
                    this.message = "";
                }
            } catch (error) {
                console.error("Lỗi khi gọi API lấy tài liệu:", error);
            }
        },
        async fetchHistoryDownload(accountId) {
            try {
                const response = await apiClient.get(`/api/history-downloads/${accountId}`);
                this.listHistoryDownload = response.data;
                if (this.listHistoryDownload.length === 0) {
                    this.message = "Bạn chưa tải tài liệu nào!";
                } else {
                    this.message = "";
                }
            } catch (error) {
                console.error("Lỗi khi gọi API lấy tài liệu:", error);
            }
        },
        formatDate(date) {
            const d = new Date(date);
            return d.toLocaleString();
        }
    }
};
</script>

<template>
    <div class="container d-flex justify-content-around pt-5 pb-5" style="width: 35%;">
        <Block v-for="item in Options" :link="item.link" :name="item.name" @click="fetchData(item.link)" />
    </div>
    <table class="table table-striped table-hover" v-if="listDocuments.length > 0 && selectedLink === 'documents'">
        <thead class="thead-dark">
            <tr>
                <th scope="col">#</th>
                <th scope="col">Tiêu đề</th>
                <th scope="col">Danh mục</th>
                <th scope="col">Thời gian tạo</th>
                <th scope="col">Thời gian sửa</th>
                <th scope="col">Trạng thái</th>
                <th scope="col" style="width: 14%;">Thao tác</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="(item, index) in listDocuments" :key="item.id">
                <th scope="row">{{ index + 1 }}</th> <!-- Đổi ID thành số thứ tự -->
                <td>{{ item.title }}</td>
                <td>{{ item.categoryName }}</td>
                <td>{{ formatDate(item.createAt) }}</td>
                <td>{{ formatDate(item.updateAt) }}</td>
                <td>
                    <span class="badge" :class="item.status === 1 ? 'badge-success' : 'badge-warning'">
                        {{ item.status === 1 ? 'Hiển thị' : 'Chờ duyệt' }}
                    </span>
                </td>
                <td class="d-flex gap-2">
                    <button class="btn btn-primary btn-sm"><i class="bi bi-pencil-square"></i> Sửa</button>
                    <button class="btn btn-danger btn-sm"><i class="bi bi-trash3-fill"></i> Xóa</button>
                </td>
            </tr>
        </tbody>
    </table>
    <table class="table table-striped table-hover" v-if="listHistoryDownload.length > 0 && selectedLink === 'history-download'">
        <thead class="thead-dark">
            <tr>
                <th scope="col">#</th>
                <th scope="col">Tiêu đề</th>
                <th scope="col">Thời gian tải</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="(item, index) in listHistoryDownload">
                <th scope="row">{{ index + 1 }}</th>
                <td>{{ item.documentName }}</td>
                <td>{{ formatDate(item.date) }}</td>
            </tr>
        </tbody>
    </table>
    <div v-if="message" class="text-center">
        <h3 class="alert alert-info">{{ message }}</h3>
        <router-link to="/my-documents/upload"><button class="btn-upload"><i class="bi bi-upload"></i> Upload
                Ngay</button></router-link>
    </div>
</template>

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

.btn-upload {
    padding: 10px 22px;
    background-color: #1976d2;
    border: none;
    color: #fff;
    text-transform: uppercase;
    font-size: 14px;
    font-weight: 600;
    transition: all 0.3s ease 0s;

    &:hover {
        background-color: rgb(18, 33, 121);
    }
}
</style>
