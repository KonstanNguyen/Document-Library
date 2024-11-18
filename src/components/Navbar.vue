<script setup>
	import { ref, onMounted, onUnmounted } from 'vue';
	const lastScrollPosition = ref(0);
	const isNavbarVisible = ref(true);

	const handleScroll = () => {
		const currentScrollPosition = window.scrollY;

		if (currentScrollPosition > lastScrollPosition.value) {
			isNavbarVisible.value = false;
		} else {
			isNavbarVisible.value = true;
		}

		lastScrollPosition.value = currentScrollPosition;
	};

	onMounted(() => {
		window.addEventListener('scroll', handleScroll);
	});

	onUnmounted(() => {
		window.removeEventListener('scroll', handleScroll);
	});
</script>

<template>
	<div
		:class="{ hidden: !isNavbarVisible }"
		class="main-header sticky-top">
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
						<input class="form-control me-2" type="search" placeholder="Tìm kiếm các sách hoặc tài liệu" aria-label="Search">
						<button class="btn-search">
							<i class="bi bi-search"></i>
						</button>
					</form>
				</div>
				<div class="col-lg-6 col-md-6 col-9">
					<div class="row">
						<div class="top-nav d-flex justify-content-end">
							<li class="navbar align-items-center">
								<router-link to="/my-documents/upload"><button class="btn-upload">+ Upload</button></router-link>
								<button class="btn-login">Đăng nhập</button>
								<!-- <div class="dropdown">
									<button
										class="btn-lang dropdown-toggle"
										type="button"
										data-bs-toggle="dropdown"
										aria-expanded="false">
										Tiếng việt
									</button>
									<ul class="dropdown-menu">
										<li>
											<a
												class="dropdown-item"
												href="#"
												>English</a
											>
										</li>
									</ul>
								</div> -->
								<button
									class="menu-icon d-sm-none"
									data-bs-toggle="offcanvas"
									href="#offcanvasExample"
									role="button"
									aria-controls="offcanvasExample">
									<i class="bi bi-list"></i>
								</button>
							</li>
						</div>
						<div
							class="main-nav d-none d-sm-flex d-flex justify-content-end gap-5">
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
	<div
		class="mobile-menu offcanvas offcanvas-start"
		tabindex="-1"
		id="offcanvasExample"
		aria-labelledby="offcanvasExampleLabel"
		style="width: 70%">
		<div class="offcanvas-header mb-3">
			<button
				type="button"
				class="btn-close"
				data-bs-dismiss="offcanvas"
				aria-label="Close"></button>
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
		.btn-login,
		.btn-lang {
			border: none;
			padding: 4px 7px;
			font-size: 14px;
			text-transform: uppercase;
			@media (max-width: 550px) {
				font-size: 12px;
				padding: 2px 5px;
			}
		}
		.btn-login {
			color: #c23410;
			background: #fff;
		}
		.btn-lang {
			border: 1px solid #fff;
			color: #fff;
			background: #c23410;
		}
		.dropdown-menu a {
			color: black;
		}
		.btn-upload{
			padding: 5px 22px;
			background-color: #1976d2;
			border: none;
			color: #fff;
			text-transform: uppercase;
			font-size: 14px;
			font-weight: 600;
			transition: all 0.3s ease 0s;
			&:hover{
				background-color: rgb(18, 33, 121);
			}
		}
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
