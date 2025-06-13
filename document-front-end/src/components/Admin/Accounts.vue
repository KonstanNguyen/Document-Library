<template>
    <div class="m-auto mt-5" style="width: 90%;">
        <table class="table table-striped table-hover">
            <thead class="thead-dark">
                <tr>
                    <th scope="col">#</th>
                    <th scope="col" style="width: 20%;">Username</th>
                    <th scope="col">Giới tính</th>
                    <th scope="col">Ngày sinh</th>
                    <th scope="col">Email</th>
                    <th scope="col">Vai trò</th>
                    <th scope="col" style="width: 20%;">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in listAccounts" :key="item.id">
                    <th scope="row">{{ index + 1 }}</th>
                    <td>{{ item.name }}</td>
                    <td>{{ item.gender ? "Nữ" : "Nam" }}</td>
                    <td>{{ formatDate(item.dateOfBirth) }}</td>
                    <td>{{ item.email }}</td>
                    <td>{{ item.roleName }}</td>
                    <td class="d-flex gap-2">
                        <button class="btn btn-primary btn-sm" @click="openEditModal(item)">
                            <i class="bi bi-pencil-square"></i>Sửa quyền
                        </button>
                        <button class="btn btn-danger btn-sm" @click="deleteAccount(item.accountId)">
                            <i class="bi bi-trash3-fill"></i> Xóa tài khoản
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
        <!-- <Pagination /> -->
    </div>

    <!-- Add Modal for Role Edit -->
    <div v-if="showEditModal" class="modal d-block" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Sửa quyền người dùng</h5>
                    <button type="button" class="btn-close" @click="showEditModal = false"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Quyền:</label>
                        <select v-model="selectedAccount.roleName" class="form-select">
                            <option v-for="role in availableRoles" :key="role.id" :value="role.name">
                                {{ role.name }}
                            </option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" @click="showEditModal = false">Đóng</button>
                    <button type="button" class="btn btn-primary" @click="updateAccountRole">Lưu thay đổi</button>
                </div>
            </div>
        </div>
        <div class="modal-backdrop fade show"></div>
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
            listAccounts: [],
            page: {
                current: 1,
                max: 1,
            },
            showEditModal: false,
            selectedAccount: null,
            availableRoles: [
                { id: 1, name: 'admin' },
                { id: 2, name: 'user' }
            ],
        }
    },
    methods: {
        async fetchData() {
            const paginationRequest = {
                page: this.page.current - 1,
                size: 9,
                sortBy: "id",
                sortDirection: "desc",
            };

            try {
                const response = await apiClient.get('/api/doc-users', { params: paginationRequest });
                const data = response.data;

                if (data && data.content) {
                    const users = data.content;
                    const usersWithRoles = await Promise.all(
                        users.map(async (user) => {
                            const role = await this.getRoleByAccount(user.accountId);
                            return { ...user, roleName: role }; // Gắn role vào từng user
                        })
                    );
                    this.listAccounts = usersWithRoles;
                    this.page.max = data.totalPages;
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        },
        async getRoleByAccount(accountId) {
            try {
                const response = await apiClient.get(`/api/accounts/getRoleByAccountId/${accountId}`);
                const roles = response.data.data;
                if (roles && roles.length > 0) {
                    return roles[0].name.toLowerCase(); // Lấy tên role thay vì id
                } else {
                    return "user"; 
                }
            } catch (error) {
                console.error("Error role:", error);
                return "user";
            }
        },
        formatDate(date) {
            const d = new Date(date);
            return d.toLocaleDateString();
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
        openEditModal(account) {
            this.selectedAccount = { ...account };
            this.showEditModal = true;
        },
        async updateAccountRole() {
            try {
                const currentRole = await this.getRoleByAccount(this.selectedAccount.accountId);
                const newRole = this.selectedAccount.roleName;
                
                if (currentRole === newRole) {
                    alert('Không có thay đổi về quyền!');
                    this.showEditModal = false;
                    return;
                }

                const currentRoleId = currentRole === 'admin' ? 1 : 2;
                const newRoleId = newRole === 'admin' ? 1 : 2;

                // Revoke current role
                await apiClient.patch(`/api/roles/${currentRoleId}/revoke/${this.selectedAccount.accountId}`);
                
                // Grant new role
                await apiClient.patch(`/api/roles/${newRoleId}/grant/${this.selectedAccount.accountId}`);
                
                await this.fetchData(); // Refresh list
                this.showEditModal = false;
                alert('Cập nhật quyền thành công!');
            } catch (error) {
                console.error('Error updating account role:', error);
                alert('Đã xảy ra lỗi khi cập nhật quyền.');
            }
        },
        async deleteAccount(accountId) {
            if (confirm('Bạn có chắc chắn muốn xóa tài khoản này?')) {
                try {
                    await apiClient.delete(`/api/accounts/${accountId}/delete`);
                    await this.fetchData(); // Refresh list
                    alert('Xóa tài khoản thành công!');
                } catch (error) {
                    console.error('Error deleting account:', error);
                    alert('Tài khoản này không thể xóa.');
                }
            }
        }
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

.modal {
    background-color: rgba(0, 0, 0, 0.5);
}

.modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: -1;
}
</style>
