<script>
import apiClient from "@/api/service";
import Block from "./Block.vue";
export default {
    components: { Block },
    data() {
        return {
            Blocks: [
                {
                    label: "Tên tài liệu",
                    type: "text",
                    value: "",
                    name: "name",
                    placeholder: "Tên tài liệu",
                },
                {
                    label: "Từ khóa",
                    type: "text",
                    value: "",
                    name: "keyword",
                    placeholder: "Để có kết quả cao tại thứ hạng tìm kiếm",
                },
            ],
            Categories: [],
            selectedFile: null,
            selectedCategory: null,
            description: "",
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
            this.selectedFile = event.target.files[0];
        },
        async submitForm() {
            if (!this.selectedFile) {
                alert("Vui lòng tải lên một file.");
                return;
            }

            const formData = new FormData();
            formData.append("file", this.selectedFile);

            // Thêm thông tin từ các block vào formData
            this.Blocks.forEach((block) => {
                formData.append(block.name, block.value);
            });

            // Lấy danh mục đã chọn
            const selectedCategory = document.querySelector(
                "select[name='categories_id']"
            ).value;
            formData.append("categories_id", selectedCategory);

            // Lấy mô tả
            const description = document.querySelector(
                "textarea[name='description']"
            ).value;
            formData.append("description", description);

            try {
                const response = await apiClient.post(
                    "/api/documents",
                    formData,
                    {
                        headers: {
                            "Content-Type": "multipart/form-data",
                        },
                    }
                );

                if (response.data && response.data.message) {
                    alert(response.data.message);
                } else {
                    alert("Tải lên tệp thành công!");
                }
                console.log(response.data);
            } catch (error) {
                console.error("Error submitting form:", error);
                if (error.response) {
                    alert(
                        `Lỗi xảy ra: ${
                            error.response.data.error || "Lỗi không xác định"
                        }`
                    );
                } else {
                    alert("Đã xảy ra lỗi. Vui lòng thử lại.");
                }
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
                    <Block
                        v-for="block in Blocks"
                        :label="block.label"
                        :type="block.type"
                        :value="block.value"
                        :name="block.name"
                        :placeholder="block.placeholder"
                    />
                    <div class="col-12 d-grid gap-1 mb-3">
                        <label>Danh mục</label>
                        <select
                            class="select-category"
                            name="categories_id"
                            id="categories"
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
