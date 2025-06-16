<script>
import apiClient from "@/api/service";
import axios from "axios";
export default {
    data() {
        return {
            formData: {
                id: null,
                title: "",
                categoryId: null,
                description: "",
                authorId: null,
                status: 0,
            },
            isEditMode: false,
            Categories: [],
            accountId: localStorage.getItem("userId") || "",
            isLoading: false,
            selectedFile: null,
            currentDocument: null, 
            showModal: false,
            showErrorModal: false,
            errorMessage: "",
            message: null,
            isSuccess: false,
            modalMessage: "",
        };
    },

    methods: {
        async getRoleByAccount(accountId) {
            try {
                const response = await apiClient.get(`/api/accounts/getRoleByAccountId/${accountId}`);
                const roles = response.data.data;
                if (roles && roles.length > 0) {
                    const role = roles[0].id;
                    this.formData.status = role === 1 ? 1 : 0;
                    return role; // Return the role for use in other methods
                }
                console.warn("No roles found for this account.");
                return null;
            }
            catch (error) {
                console.error("Error role:", error);
                return null;
            }
        },
        async fetchCategories() {
            try {
                const response = await apiClient.get("/category");
                this.Categories = response.data;
            } catch (error) {
                console.error("Error fetching categories:", error);
            }
        },
        async fetchAuthorId(accountId) {
            try {
                const response = await apiClient.get(`/api/accounts/${accountId}`);
                this.formData.authorId = response.data.userId;
            } catch (error) {
                console.error("Lỗi khi gọi API lấy thông tin tài khoản:", error);
                this.message = "Không thể tải thông tin người dùng.";
            }
        },
        async fetchDocumentData(id) {
            try {
                const response = await apiClient.get(`/api/documents/${id}`);
                this.currentDocument = response.data;
                console.log("Fetched document:", this.currentDocument);

                this.formData = {
                    id: this.currentDocument.id,
                    title: this.currentDocument.title,
                    categoryId: Number(this.currentDocument.categoryId), 
                    description: this.currentDocument.description || '',
                    authorId: this.currentDocument.authorId, 
                    status: this.currentDocument.status
                };
                
                console.log("Form data after update:", this.formData);
            } catch (error) {
                console.error("Error fetching document:", error);
            }
        },
        handleFileUpload(event) {
            // this.formData.file = file;
            // const file = event.target.files[0];
            this.selectedFile = event.target.files[0];
            if (!this.selectedFile) {
                alert("Vui lòng chọn một file hợp lệ.");
                return;
            }
            console.log("File được chọn:", this.selectedFile);
        },
        async submitForm() {
            this.validateForm();
            if (this.$refs.form.checkValidity() === false) {
                this.isLoading = false;
                this.message = "Vui lòng điền đầy đủ thông tin.";
                this.isSuccess = false;
                this.hideMessageAfterDelay();
                return;
            }
            if (!this.isTitleValid()) {
                this.isLoading = false;
                this.message = "Vui lòng nhập tiêu đề hợp lệ.";
                this.isSuccess = false;
                this.hideMessageAfterDelay();
                return;
            }
            this.isLoading = true;
            try {
                const role = await this.getRoleByAccount(this.accountId);
                const formDataToSend = new FormData();

                const data = {
                    title: this.formData.title,
                    categoryId: this.formData.categoryId,
                    authorId: this.formData.authorId,
                    description: this.formData.description,
                    status: this.formData.status,
                };

                if (this.isEditMode) {
                    if (this.selectedFile) {
                        formDataToSend.append("file", this.selectedFile);
                    }
                    formDataToSend.append("data", new Blob([JSON.stringify(data)], { type: "application/json" }));
                    
                    await apiClient.put(`/api/documents/${this.formData.id}/update`, formDataToSend, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                            'Authorization': localStorage.getItem("token"),
                        },
                    });
                    alert("Cập nhật tài liệu thành công!");
                } else {
                    formDataToSend.append("file", this.selectedFile);
                    formDataToSend.append("data", new Blob([JSON.stringify(data)], { type: "application/json" }));
                    
                    await axios.post('http://localhost:8080/api/documents/create', formDataToSend, {
                        headers: {
                            'Content-Type': 'multipart/form-data',
                            "Access-Control-Allow-Origin": "*",
                            'Authorization': localStorage.getItem("token"),
                        },
                    });

                    if (role === 1) {
                        this.modalMessage = "Tải lên thành công!";
                    } else {
                        this.modalMessage = "Tài liệu của bạn đã được gửi cho quản trị viên để duyệt!";
                    }
                    this.showModal = true;
                }
                
            } catch (error) {
                console.error("Error submitting form:", error);
                alert("Đã xảy ra lỗi trong quá trình xử lý.");
            } finally {
                this.isLoading = false;
            }
        },
        isTitleValid() {
            const startsWithSpecialChar = /^[!@#$%^&*(),.?":{}|<>]/.test(this.formData.title);
            const exceedsMaxLength = this.formData.title.length > 100;
            return !(startsWithSpecialChar || exceedsMaxLength);
        },

        closeModal() {
            this.showModal = false;
            this.showErrorModal = false;
            this.$forceUpdate();
        },
        validateForm() {
            const form = this.$refs.form;
            if (form) {
                form.classList.add('was-validated');
            }
        },
        showErrorModal(message) {
            this.errorMessage = message;
            this.showErrorModal = true;
            this.hideMessageAfterDelay();
        },
        hideMessageAfterDelay() {
            setTimeout(() => {
                this.message = null;
                this.isSuccess = false;
            }, 2000);
        }
    },
    async created() {
        try {
            await this.fetchCategories();
            console.log("Categories loaded:", this.Categories); 

            if (this.$route.params.id) {
                this.isEditMode = true;
                await this.fetchDocumentData(this.$route.params.id);
            }

            await this.getRoleByAccount(this.accountId);
            await this.fetchAuthorId(this.accountId);
        } catch (error) {
            console.error("Error in created hook:", error);
        }
    },
    watch: {
        Categories: {
            handler(newCategories) {
                console.log("Categories updated:", newCategories);
            },
            immediate: true
        },
        'formData.categoryId': {
            handler(newVal) {
                console.log("Category ID changed to:", newVal);
            }
        },
        formData: {
            deep: true,
            handler(newVal) {
                console.log("Form data changed:", newVal);
            }
        }
    }
};
</script>

<template>
    <div class="apply-course">
        <div class="wrapper">
            <h2 class="text-center mb-4">{{ isEditMode ? 'Sửa tài liệu' : 'Tải lên tài liệu' }}</h2>
            <form ref="form" @submit.prevent="submitForm" enctype="multipart/form-data" class="needs-validation" novalidate>
                <div class="row">
                    <div class="col-12 mb-3">
                        <label for="title" class="form-label">Tiêu đề</label>
                        <input id="title" type="text" class="form-control" placeholder="Nhập tiêu đề"
                            v-model="formData.title" required/>
                        <div class="invalid-feedback">
                            Vui lòng nhập tiêu đề tài liệu.
                        </div>
                    </div>
                    <div class="col-12 d-grid gap-1 mb-3">
                        <label>Danh mục</label>
                        <select 
                            class="select-category" 
                            name="categoryId" 
                            id="categories" 
                            v-model.number="formData.categoryId" required
                        >
                            <option value="" disabled>Chọn danh mục</option>
                            <option 
                                v-for="item in Categories" 
                                :key="item.id" 
                                :value="item.id"
                            >
                                {{ item.name }}
                            </option>
                        </select>
                        <div class="invalid-feedback">
                            Vui lòng chọn danh mục tài liệu.
                        </div>
                    </div>
                    <div class="col-12 mb-3">
                        <label for="formFile" class="form-label">
                            {{ isEditMode ? 'Thay đổi file (không bắt buộc)' : 'Upload file' }}
                        </label>
                        <input class="upload form-control" type="file" id="formFile" @change="handleFileUpload" required/>
                        <div class="invalid-feedback">
                            Vui lòng upload file tài liệu.
                        </div>
                        <small v-if="isEditMode && currentDocument" class="text-muted">
                            File hiện tại: {{ currentDocument.content?.split('/').pop() }}
                        </small>
                    </div>
                    <div class="col-12 mb-3">
                        <label class="form-label">Mô tả</label>
                        <textarea class="description form-control" type="text" placeholder="Nhập mô tả"
                            name="description" v-model="formData.description"></textarea>
                    </div>
                    <div class="message" v-if="message !== null && message !== ''">
                        <div v-if="isSuccess" class="alert alert-success">
                            {{ message }}
                        </div>
                        <div v-else class="alert alert-danger">{{ message }}</div>
                    </div>
                    <div class="col-12 text-center pt-4">
                        <button class="btn-apply" type="submit" :disabled="isLoading">
                            <span v-if="isLoading" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                            Lưu thông tin
                        </button>
                    </div>
                </div>
            </form>
        </div>
        <div v-if="showModal" class="modal">
        <div class="modal-content">
            <span class="close" @click="closeModal">&times;</span>
            <p>{{ modalMessage }}</p>
            <div class="d-flex justify-content-around">
                <button @click="closeModal" class="btn btn-outline-danger">Đóng</button>
                <router-link to="/my-documents/documents">
                    <button class="btn btn-outline-primary">
                        Xem danh sách tài liệu
                    </button>
                </router-link>
            </div>
        </div>
    </div>
    <div v-show="showErrorModal" class="modal">
        <div class="modal-content">
            <span class="close" @click="closeModal">&times;</span>
            <p>{{ errorMessage }}</p>
            <div class="d-flex justify-content-around">
                <button @click="closeModal" class="btn btn-outline-danger">Đóng</button>
            </div>
        </div>
    </div>
    </div>
</template>

<style scoped lang="scss">
.apply-course .wrapper {
    font-size: 17px;
    padding: 40px;
    border: 1px solid rgba(0, 0, 0, 0.1);
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}

.select-category {
    width: 100%;
    border: none;
    background-color: #e9ecef;
    padding: 10px 20px;
}

.btn-apply {
    background-color: #1976d2;
    border: 2px solid #fff;
    padding: 10px 20px;
    color: #fff;
    border-radius: 0;
    font-size: 14px;
    font-weight: 600;
    line-height: 20px;
    text-transform: uppercase;
    transition: all 0.3s ease 0s;

    &:hover {
        background-color: #0e1a61;
    }
}

.description {
    background: #e9ecef;
    height: 100px;
    border: 0;
    font-size: 14px;
    width: 100%;
    padding: 10px 20px;
}

.upload {
    background: #e9ecef;
}
.spinner-border {
    margin-right: 5px;
    width: 1rem;
    height: 1rem;
    border-width: 0.2em;
}
.modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 5px;
    width: 400px;
    text-align: center;
    position: relative;
}

.close {
    position: absolute;
    top: 10px;
    right: 10px;
    font-size: 30px;
    cursor: pointer;
}
</style>
