<script>
export default {
    props: {
        id: {
            type: String,
            required: true,
        },
        title: {
            type: String,
            required: true,
        },
        category: {
            type: String,
            required: true,
        },
        create_at: {
            type: Date,
            required: true,
        },
        update_at: {
            type: Date,
            required: true,
        },
        status: {
            type: Number,
            required: true,
        }
    },
    data() {
        return {
            listDocuments: [
                {
                    id: '01',
                    title: "[NCKH] thiết kế nghiên cứu khoa học",
                    category: "Khoa học",
                    create_at: "20/01/2024",
                    update_at: "20/01/2024",
                    status: 1,
                },
                {
                    id: '02',
                    title: "2024 Trend Updates: What Really Works In SEO & Content Marketing",
                    category: "Marketing",
                    create_at: "20/01/2024",
                    update_at: "20/01/2024",
                    status: 1,
                },
                {
                    id: '03',
                    title: "Phương pháp luận sáng tạo và đổi mới (TRIZ mở rộng)",
                    category: "Khoa học",
                    create_at: "20/01/2024",
                    update_at: "21/01/2024",
                    status: 1,
                },
            ]
        }
    }
};
</script>

<template>
    <table class="table">
        <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Tiêu đề</th>
                <th scope="col">Danh mục</th>
                <th scope="col">Thời gian tạo</th>
                <th scope="col">Thời gian sửa</th>
                <th scope="col">Trạng thái</th>
                <th scope="col">Thao tác</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="item in listDocuments">
                <th scope="row">{{ item.id }}</th>
                <td>{{ item.title }}</td>
                <td>{{ item.category }}</td>
                <td>{{ item.create_at }}</td>
                <td>{{ item.update_at }}</td>
                <td>{{ item.status === 1 ? "Hiển thị" : "Chờ duyệt" }}</td>
                <td class="d-flex gap-3">
                    <button class="btn-edit"><i class="bi bi-pencil-square h5"></i></button>
                    <button class="btn-delete"><i class="bi bi-trash3-fill h5"></i></button>
                </td>
            </tr>
        </tbody>
    </table>
</template>

<style scoped lang="scss">
.btn-edit,
.btn-delete {
    padding: 5px 10px;
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
