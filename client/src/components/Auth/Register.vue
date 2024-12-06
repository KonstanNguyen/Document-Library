<style scoped lang="scss">
.form {
	background-color: #fff;
	border-radius: 15px;
}

.container {
	max-width: 500px;
	margin: 0 auto;
	padding: 30px;
}

.btn {
	width: 100%;
}
</style>
<template>
	<div class="form container mt-3">
		<h2>Đăng ký</h2>
		<form @submit.prevent="register">
			<div class="form-group mb-3">
				<label for="username">Tên tài khoản</label>
				<input type="text" class="form-control" id="username" placeholder="Tài khoản" v-model="username"
					required />
			</div>
			<div class="form-group mb-3">
				<label for="email">Email</label>
				<input type="text" class="form-control" id="email" placeholder="Nhập email" v-model="email" required />
			</div>
			<div class="form-group mb-3">
				<label for="password">Mật khẩu</label>
				<input type="password" class="form-control" id="password" placeholder="Mật khẩu" v-model="password"
					required />
			</div>
			<div class="form-group mb-3">
				<label for="password">Xác nhận mật khẩu</label>
				<input type="password" class="form-control" id="password" placeholder="Xác nhận mật khẩu"
					v-model="confirmPassword" required />
			</div>
			<div class="form-group mb-3">
				<label for="age">Ngày sinh</label>
				<!-- <VueDatePicker v-model="dateOfBirth" 
				:format="dateFormat"
				:disable-time-selection="true"
				:start-date="startDate"
				:min-date="minDate" 
  				:max-date="maxDate"
				 /> -->
				<input class="form-control" type="date" id="age" v-model="dateOfBirth" />
			</div>
			<div class="form-group mb-3">
				<label for="gender">Giới tính</label>
				<select class="form-select" id="gender" v-model="gender" required>
					<option selected disabled>Chọn giới tính</option>
					<option value="false">Nam</option>
					<option value="true">Nữ</option>
				</select>
			</div>
			<div class="message" v-if="message !== null && message !== ''">
                <div v-if="isSuccess" class="alert alert-success">
                    {{ message }}
                </div>
                <div v-else class="alert alert-danger">{{ message }}</div>
            </div>

			<button type="submit" class="btn btn-primary mb-2">
				Đăng Ký
			</button>
			<router-link to="/login">
				<button type="submit" class="btn btn-primary">
					Đăng nhập
				</button>
			</router-link>
		</form>
	</div>
</template>

<script>
import apiClient from "@/api/service";
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'
export default {
	components: {
		VueDatePicker,
	},
	data() {
		return {
			username: "",
			email: "",
			password: "",
			confirmPassword: "",
			dateOfBirth: null,
			gender: null,
			message: "",
			dateFormat: "yyyy-MM-dd",
			minDate: new Date(1945, 0, 1),
			maxDate: new Date(2012, 0, 1),
			startDate: new Date(2000, 0, 1),
			isSuccess: false,
		};
	},
	methods: {
		async register(event) {
			event.preventDefault();

			if (this.password !== this.confirmPassword) {
				this.message = "Mật khẩu và xác nhận mật khẩu không khớp!";
				return;
			}

			const date = new Date(this.dateOfBirth);
			const day = String(date.getDate()).padStart(2, "0"); // Ensure 2 digits
			const month = String(date.getMonth() + 1).padStart(2, "0"); // Months are zero-indexed
			const year = date.getFullYear();

			const requestBody = {
				username: this.username,
				email: this.email,
				password: this.password,
				birthday: `${year}-${month}-${day}`,
				gender: this.gender === "true" ? true : false,
			};

			try {
				const response = await apiClient.post("/api/accounts/register", requestBody);

				if (response.status === 201 || response.status === 200) {
					this.message = "Đăng ký thành công!";
					this.isSuccess = true;
				} else {
					this.message = `Đăng ký thất bại! Mã lỗi: ${response.status}`;
					this.isSuccess = false;
				}
			} catch (error) {
				this.message = "Đã xảy ra lỗi khi đăng ký!";
				this.isSuccess = false;
				console.error(error);
			}
		},
	},
};
</script>