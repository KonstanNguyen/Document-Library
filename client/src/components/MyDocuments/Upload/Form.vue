<script>
import apiClient from "@/api/service";
export default {
    data() {
        return {
            formData: {
                title: "",
                categoryId: null,
                // description: "",
                authorId: 3,
                status: 0,
            },
            Categories: [],
        };
    },

    methods: {
        async fetchCategories() {
            try {
                const response = await apiClient.get("/category");
                this.Categories = response.data;
            } catch (error) {
                console.error("Error fetching categories:", error);
            }
        },
        handleFileUpload(event) {
            const file = event.target.files[0];
            if (!file) {
                alert("Vui lòng chọn một file hợp lệ.");
                return;
            }
            this.formData.file = file;
            console.log("File được chọn:", file.name);
        },
        async submitForm() {
            try {
                const formDataToSend = new FormData();
                
                const data = {
                    title: this.formData.title,
                    categoryId: this.formData.categoryId,
                    authorId: this.formData.authorId,
                    status: this.formData.status,
                };
                formDataToSend.append("data", new Blob([JSON.stringify(data)], { type: "application/json" }));
                formDataToSend.append("file", this.formData.file);

                console.log("Payload:", JSON.stringify(this.formData));

                // Gửi dữ liệu đến backend
                const response = await apiClient.post("/api/documents", formDataToSend, {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                });
                alert("Tải lên thành công!");
                console.log("Kết quả:", response.data);
            } catch (error) {
                console.error("Error submitting form:", error);
                alert("Đã xảy ra lỗi trong quá trình tải lên.");
            }
        },
    },
    mounted() {
        this.fetchCategories();
    },
};
</script>

<template>
    <div class="apply-course">
        <div class="wrapper">
            <form @submit.prevent="submitForm" enctype="multipart/form-data">
                <div class="row">
                    <div class="col-12 mb-3">
                        <label for="title" class="form-label">Tiêu đề</label>
                        <input
                            id="title"
                            type="text"
                            class="form-control"
                            placeholder="Nhập tiêu đề"
                            v-model="formData.title"
                        />
                    </div>
                    <div class="col-12 d-grid gap-1 mb-3">
                        <label>Danh mục</label>
                        <select
                            class="select-category"
                            name="categoryId"
                            id="categories"
                            v-model="formData.categoryId"
                        >
                            <option v-for="item in Categories" :value="item.id">
                                {{ item.name }}
                            </option>
                        </select>
                    </div>
                    <div class="col-12 mb-3">
                        <label for="formFile" class="form-label"
                            >Upload file</label
                        >
                        <input
                            class="upload form-control"
                            type="file"
                            id="formFile"
                            @change="handleFileUpload"
                        />
                    </div>
                    <div class="col-12 mb-3">
                        <label class="form-label">Mô tả</label>
                        <textarea
                            class="description form-control"
                            type="text"
                            placeholder="Nhập mô tả"
                            name="description"
                            v-model="formData.description"
                        ></textarea>
                    </div>
                    <div class="col-12 text-center pt-4">
                        <button class="btn-apply" type="submit">
                            Lưu thông tin
                        </button>
                    </div>
                </div>
            </form>
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
</style>
