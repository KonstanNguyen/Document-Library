<script>
import apiClient from "@/api/service";
export default {
	props: {
		username: {
			type: String,
			default: null,
		},
	},
	data() {
		return {
			accountId: localStorage.getItem("userId") || "",
			roleName: "",
			lastScrollPosition: 0,
			isNavbarVisible: true,
		};
	},
	methods: {
		handleScroll() {
			const currentScrollPosition = window.scrollY;

			if (currentScrollPosition > this.lastScrollPosition) {
				this.isNavbarVisible = false;
			} else {
				this.isNavbarVisible = true;
			}

			this.lastScrollPosition = currentScrollPosition;
		},
		confirmLogout() {
			const confirmLogout = window.confirm("Bạn có chắc muốn đăng xuất?");
			if (confirmLogout) {
				this.logout();
			}
		},
		logout() {
			localStorage.removeItem('token');
			localStorage.removeItem('username');
			localStorage.removeItem('userId');
			window.location.href = '/';
		},

		async getRoleByAccount(accountId) {
			try {
				const response = await apiClient.get(`/api/accounts/getRoleById/${accountId}`);
				const roles = response.data.data;
				if (roles && roles.length > 0) {
					const role = roles[0].id;
					this.roleName = role === 1 ? "admin" : "user";
					localStorage.setItem("roleName", this.roleName);
				} else {
					console.warn("No roles found for this account.");
				}
			}
			catch (error) {
				console.error("Error role:", error);
			}
		},
	},
	mounted() {
		window.addEventListener('scroll', this.handleScroll);
		if (this.accountId) {
			this.getRoleByAccount(this.accountId);
		}
	},
	beforeUnmount() {
		window.removeEventListener('scroll', this.handleScroll);
	},
};
</script>


<template>
	<div :class="{ hidden: !isNavbarVisible }" class="main-header sticky-top">
		<div class="container">
			<div class="row align-items-center">
				<div class="col-lg-2 col-xl-2 col-md-6 col-3">
					<div class="header-logo d-flex align-items-center">
						<router-link to="/" style="color: white; font-weight: bold;">
							THƯ VIỆN TÀI LIỆU
						</router-link>
					</div>
				</div>
				<div class="col-4">
					<form class="d-flex" role="search">
						<input class="form-control me-2" type="search" placeholder="Tìm kiếm các sách hoặc tài liệu"
							aria-label="Search">
						<button class="btn-search">
							<i class="bi bi-search"></i>
						</button>
					</form>
				</div>
				<div class="col-lg-6 col-md-6 col-9">
					<div class="row">
						<div class="top-nav d-flex justify-content-end">
							<li class="navbar align-items-center">
								<router-link to="/my-documents/upload"><button class="btn-upload"><i
											class="bi bi-upload"></i> Upload</button></router-link>

								<div v-if="username" class="dropdown">
									<button class="btn btn-secondary dropdown-toggle" type="button" id="userDropdown"
										data-bs-toggle="dropdown" aria-expanded="false">
										<i class="bi bi-person-circle"></i> {{ username }}
									</button>
									<ul class="dropdown-menu" aria-labelledby="userDropdown">
										<li v-if="roleName === 'admin'">
											<router-link to="/admin/documents/all"><button class="dropdown-item">Quản
													lý tài liệu</button></router-link>
											<router-link to="/admin/accounts"><button class="dropdown-item">Quản
													lý tài khoản</button></router-link>
											<router-link to="/admin/history-download"><button class="dropdown-item">Quản
												lý tải xuống</button></router-link>
										</li>
										<li>
											<button class="dropdown-item" @click="confirmLogout">Đăng xuất</button>
										</li>
									</ul>
								</div>

								<router-link to="/login" v-if="!username">
									<button class="btn-login">Đăng nhập</button>
								</router-link>

								<button class="menu-icon d-sm-none" data-bs-toggle="offcanvas" href="#offcanvasExample"
									role="button" aria-controls="offcanvasExample">
									<i class="bi bi-list"></i>
								</button>
							</li>
						</div>
						<div class="main-nav d-none d-sm-flex d-flex justify-content-end gap-5">
							<ul class="navbar align-items-center gap-5">
								<li>
									<router-link to="/">
										<i class="bi bi-house-door-fill"></i>
										Trang chủ
									</router-link>
								</li>
								<li>
									<router-link to="/my-documents">
										Thư viện của tôi
									</router-link>
								</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="mobile-menu offcanvas offcanvas-start" tabindex="-1" id="offcanvasExample"
		aria-labelledby="offcanvasExampleLabel" style="width: 70%">
		<div class="offcanvas-header mb-3">
			<button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
		</div>
		<div class="offcanvas-body d-flex flex-column gap-5">
			<li>
				<router-link to="/">
					<i class="bi bi-house-door-fill"></i>
					Trang chủ
				</router-link>
			</li>
			<li>
				<router-link to="/my-documents"> Thư viện của tôi </router-link>
			</li>
		</div>
	</div>
</template>

<style scoped lang="scss">
.main-header {
	background-color: #666;
	transition: transform 0.3s ease-in-out;
	transform: translateY(0);

	.navbar {

		a,
		li {
			color: #fff;
			text-transform: uppercase;
			font-size: 14px;
			font-weight: 600;
		}
	}

	.top-nav {
		.navbar {
			gap: 0.5rem;

			@media (max-width: 550px) {
				gap: 5px;
			}
		}
	}

	.menu-icon {
		background-color: transparent;
		color: #fff;
		border: none;
		font-size: 30px;
	}

	.btn-search {
		color: #fff;
		background: transparent;
		border: none;
		display: flex;
		font-size: 26px;
		transition: all 0.3s ease 0s;

		&:hover {
			color: var(--bs-primary);
		}

		@media (max-width: 550px) {
			font-size: 16px;
		}
	}

	.btn-login {
		padding: 5px 22px;
		color: #1976d2;
		background: #fff;
		text-transform: uppercase;
		border: none;
		font-size: 14px;
		font-weight: 600;
		transition: all 0.3s ease 0s;

		&:hover {
			background-color: #1976d2;
			color: #fff;
		}
	}

	.btn-upload {
		padding: 5px 22px;
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
}

.dropdown-toggle {
	display: flex;
	align-items: center;
}

.dropdown-toggle i {
	margin-right: 8px;
}

.dropdown-menu {
	min-width: 200px;
}
.dropdown-item{
	font-size: 16px;
	text-transform: capitalize;
}

.mobile-menu {
	a {
		color: #252525;
		font-weight: 600;
		text-transform: uppercase;
	}
}

.hidden {
	transform: translateY(-100%);
}
</style>
