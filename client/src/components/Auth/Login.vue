<style scoped lang="scss">
    .form{
        background-color: #fff;
        border-radius: 15px;
    }
	.container {
		max-width: 400px;
		margin: 0 auto;
		padding: 30px;
	}
    .btn{
        width: 100%;
    }
</style>
<template>
	<div class="form container mt-3">
		<h2>Đăng nhập</h2>
		<form @submit="login">
			<div class="form-group mb-4">
				<label for="username">Tên tài khoản</label>
				<input
					type="text"
					class="form-control"
					id="username"
                    placeholder="Tài khoản"
					v-model="username"
					required />
			</div>
			<div class="form-group mb-4">
				<label for="password">Mật khẩu</label>
				<input
					type="password"
					class="form-control"
					id="password"
                    placeholder="Mật khẩu"
					v-model="password"
					required />
			</div>
            <div class="d-flex justify-content-between ">
                <div class="mb-4 form-check">
                    <input type="checkbox" class="form-check-input" id="exampleCheck1">
                    <label class="form-check-label" for="exampleCheck1">Nhớ tài khoản</label>
                </div>
                <p style="text-decoration: underline;">Quên mật khẩu</p>
            </div>  
			<div
				class="message"
				v-if="message != null && message != ''">
				{{ message }}
			</div>
			<button
				type="submit"
				class="btn btn-primary mb-2">
				Đăng nhập
			</button>
            <router-link to="/register">
                <button
                    type="submit"
                    class="btn btn-primary">
                    Đăng Ký
                </button>
            </router-link>
		</form>
	</div>
</template>

<script>
	import { useCookies } from '@vueuse/integrations/useCookies';
	import { jwtDecode } from 'jwt-decode';
	export default {
		data() {
			return {
				username: '',
				password: '',
				inLoad: false,
				message: '',
				isVerified: false,
			};
		},
		setup() {
			const cookies = useCookies();
			return { cookies };
		},
		methods: {
			async login(event) {
				event.preventDefault();
				this.inLoad = true;

				const loginData = {
					username: this.username,
					password: this.password,
				};

				await fetch(`${this.serverUrl}/api/auth/login`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
					},
					body: JSON.stringify(loginData),
					credentials: 'include',
				})
					.then((response) => response.json())
					.then((data) => {
						this.inLoad = false;
						if (data.status === 200) {
							console.log('Đăng nhập thành công!');
						} else throw new Error(data.message);
						this.cookies.set('accessToken', data.accessToken, {
							path: '/',
							expires: new Date(
								new Date().getTime() + 60 * 60 * 1000
							), // 1 hour
						});
						this.cookies.set('refreshToken', data.refreshToken, {
							path: '/',
							expires: new Date(
								new Date().getTime() + 60 * 60 * 1000 * 24 * 7
							),
						});

						const accessToken = this.cookies.get('accessToken');

						const decodedToken = jwtDecode(accessToken);
						const user = {
							uid: decodedToken.uid,
							username: decodedToken.username,
							permissions: decodedToken.permissions,
						};
						this.cookies.set('user', JSON.stringify(user), {
							path: '/admin',
							expires: new Date(
								new Date().getTime() + 60 * 60 * 1000 * 24 * 7
							),
						});

						const refreshToken = this.cookies.get('refreshToken');

						if (!accessToken || !refreshToken) {
							throw new Error(
								'Lỗi đăng nhập, không nhận được token!'
							);
						}
						this.isVerified = true;
						this.$emit('login-success', username);
						console.log(
							'Đăng nhập thành công và token đã được lưu trong cookies'
						);
					})
					.catch((error) => {
						// Handle any errors
						this.message = error;
						console.error(error);
					});
			},
		},
	};
</script>
