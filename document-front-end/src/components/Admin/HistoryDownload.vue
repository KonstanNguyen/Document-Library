<template>
    <div class="m-auto mt-5" style="width: 90%;">
        <!-- Add export controls -->
        <div class="d-flex justify-content-end mb-3 gap-2">
            <input 
                type="datetime-local" 
                class="form-control" 
                style="width: auto;"
                v-model="startDate" 
                placeholder="Từ ngày"
            />
            <input 
                type="datetime-local" 
                class="form-control" 
                style="width: auto;"
                v-model="endDate" 
                placeholder="Đến ngày"
            />
            <select class="form-select" style="width: auto;" v-model="selectedFormat">
                <option value="">Chọn định dạng</option>
                <option value="csv">CSV</option>
                <option value="excel">Excel</option>
                <option value="pdf">PDF</option>
                <option value="docx">DOCX</option>
                <option value="json">JSON</option>
                <option value="xml">XML</option>
                <option value="txt">TXT</option>
            </select>
            <button 
                class="btn btn-success" 
                @click="exportHistoryDownloads"
                :disabled="!selectedFormat">
                <i class="bi bi-download"></i> Xuất file
            </button>
        </div>

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
        <!-- <Pagination /> -->
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
            selectedFormat: '', // Add this new data property
            startDate: '',
            endDate: '',
        }
    },
    methods: {
        async fetchData() {
            const paginationRequest = {
                page: this.page.current - 1,
                size: 9,
                sortBy: "date",
                sortDirection: "desc",
            };

            try {
                const response = await apiClient.get('/api/history-downloads', { params: paginationRequest });
                const data = response.data;
                this.listHistoryDownload = data;
                this.page.max = data.totalPages;
            } catch (error) {
                console.error('Error fetching data:', error);
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
        async exportHistoryDownloads() {
            if (!this.selectedFormat) {
                alert('Vui lòng chọn định dạng xuất file!');
                return;
            }
            
            try {
                const fileName = `history-downloads_${new Date().toISOString().slice(0,10)}`;
                let params = { fileName };
                
                if (this.startDate && this.endDate) {
                    params.startDate = this.startDate;
                    params.endDate = this.endDate;
                }
                
                const response = await apiClient.get(
                    `/api/history-downloads/export/${this.selectedFormat}`, 
                    { 
                        params,
                        responseType: 'blob'
                    }
                );
                
                // Create blob link to download
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', `${fileName}.${this.selectedFormat}`);
                document.body.appendChild(link);
                link.click();
                
                // Clean up
                link.parentNode.removeChild(link);
                window.URL.revokeObjectURL(url);
            } catch (error) {
                console.error('Error exporting history downloads:', error);
                alert('Có lỗi xảy ra khi xuất file!');
            }
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

.form-select {
    min-width: 150px;
}

.btn-success {
    background-color: #28a745;
    border-color: #28a745;
    &:disabled {
        background-color: #6c757d;
        border-color: #6c757d;
    }
}
</style>
