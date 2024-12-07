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
                    <th scope="col" style="width: 15%;">Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(item, index) in listAccounts" :key="item.id">
                    <th scope="row">{{ index + 1 }}</th>
                    <td>{{ item.name }}</td>
                    <td>{{ item.gender ? "Nam" : "Nữ" }}</td>
                    <td>{{ formatDate(item.dateOfBirth) }}</td>
                    <td>{{ item.email }}</td>
                    <td>{{ item.roleName }}</td>
                    <td>
                        <button class="btn btn-primary btn-sm"><i class="bi bi-pencil-square"></i>Chỉnh sửa</button>
                    </td>
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
            listAccounts: [],
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
                const response = await apiClient.get(`/api/accounts/getRoleById/${accountId}`);
                const roles = response.data.data;
                if (roles && roles.length > 0) {
                    const role = roles[0].id;
                    return role === 1 ? "admin" : "user";
                } else {
                    console.warn("No roles found for this account.");
                    return "unknown"; 
                }
            } catch (error) {
                console.error("Error role:", error);
                return "unknown";
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
