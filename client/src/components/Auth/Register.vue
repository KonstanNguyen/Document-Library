<style scoped lang="scss">
    .form{
        background-color: #fff;
        border-radius: 15px;
    }
	.container {
		max-width: 500px;
		margin: 0 auto;
		padding: 30px;
	}
    .btn{
        width: 100%;
    }
</style>
<template>
	<div class="form container mt-3">
		<h2>Đăng ký</h2>
		<form @submit="register">
			<div class="form-group mb-3">
				<label for="username">Tên tài khoản</label>
				<input
					type="text"
					class="form-control"
					id="username"
                    placeholder="Tài khoản"
					v-model="username"
					required />
			</div>
            <div class="form-group mb-3">
				<label for="email">Email</label>
				<input
					type="text"
					class="form-control"
					id="email"
                    placeholder="Nhập email"
					v-model="email"
					required />
			</div>
			<div class="form-group mb-3">
				<label for="password">Mật khẩu</label>
				<input
					type="password"
					class="form-control"
					id="password"
                    placeholder="Mật khẩu"
					v-model="password"
					required />
			</div>
            <div class="form-group mb-3">
				<label for="password">Xác nhận mật khẩu</label>
				<input
					type="password"
					class="form-control"
					id="password"
                    placeholder="Xác nhận mật khẩu"
					v-model="password"
					required />
			</div>  
			<div class="form-group mb-3">
				<label for="age">Năm sinh</label>
				<select class="form-select" 
					id="year-select" v-model="selectedYear" @change="onYearChange">
					<option selected disabled>Chọn năm sinh</option>
					<option v-for="year in years" :key="year" :value="year">{{ year }}</option>
				</select>
			</div>
			<div class="form-group mb-3">
				<label for="dateOfBirth">Giới tính</label>
				<select class="form-select" aria-label="Large select example">
					<option selected disabled>Chọn giới tính</option>
					<option value="0">Nam</option>
					<option value="1">Nữ</option>
				</select>
			</div>
			<div
				class="message"
				v-if="message != null && message != ''">
				{{ message }}
			</div>
            
            <router-link to="/register">
                <button
                    type="submit"
                    class="btn btn-primary mb-2">
                    Đăng Ký
                </button>
            </router-link>
			<router-link to="login">
                <button
                    type="submit"
                    class="btn btn-primary">
                    Đăng nhập
                </button>
            </router-link>
		</form>
	</div>
</template>

<script>
import { ref } from "vue";

export default {
  name: "YearSelect",
  props: {
    startYear: {
      type: Number,
      default: 1900, // Default start year
    },
    endYear: {
      type: Number,
      default: new Date().getFullYear(), // Default to current year
    },
    defaultYear: {
      type: Number,
      default: null, // Optionally pre-select a year
    },
  },
  setup(props, { emit }) {
    // Generate years in descending order
    const years = ref(
      Array.from({ length: props.endYear - props.startYear + 1 }, (_, i) =>
        props.endYear - i
      )
    );

    // Selected year state
    const selectedYear = ref(props.defaultYear);

    // Emit the selected year on change
    const onYearChange = () => {
      emit("year-selected", selectedYear.value);
    };

    return {
      years,
      selectedYear,
      onYearChange,
    };
  },
};
</script>