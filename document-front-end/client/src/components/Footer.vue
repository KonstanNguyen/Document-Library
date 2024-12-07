<script>
export default {
    data(){
        return {
            footerLang: this.langPack.footer,
        };
    },
}
</script>

<template>
    <div class="footer pt-5">
        <div class="container">
            <div class="row">
                <div class="logo col-12 col-sm-12 col-md-4 col-lg-2 fw-bold">
                    THƯ VIỆN TÀI LIỆU
                </div>
                <div class="info col-12 col-sm-6 col-md-8 col-lg-7 d-grid gap-1">
                    <div class="phone">
                        <i class="bi bi-telephone-fill"></i>
                        {{ footerLang.copyright.tel }}
                    </div>
                    <div class="email">
                        <i class="bi bi-envelope-fill"></i>
                        {{ footerLang.copyright.email }}
                    </div>
                </div>
                <div class="col-12 col-sm-12 col-md-12 col-lg-3">
                    <div class="social d-flex gap-4 pb-4">
                        <i class="bi bi-facebook h4"></i>
                        <i class="bi bi-youtube h4"></i>
                        <i class="bi bi-linkedin h4"></i>
                    </div>
                    <div class="contact">
                        <p>Đăng ký để nhận tin mới nhất</p>
                        <form class="d-flex align-items-center gap-2">
                            <div>
                                <input type="email" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp"
                                placeholder="Nhập email của bạn">
                            </div>
                            <button class="btn-send" type="submit">
                                <i class="bi bi-send"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="copyright mt-5">
        <div class="d-flex">
            <div class="col-12 text-center">
                <p>Copyright © 2024</p>
            </div>
        </div>
    </div>
</template>

<style scoped lang="scss">
    .footer{
        .logo img{
            @media (max-width: 550px) {
                position: relative;
                left: 50%;
                transform: translate(-50%);
                margin-bottom: 1rem;
            }
        }
        .info{
            font-size: 16px;
            font-weight: 600;
            color: #424242;
        }
        i {
            color:#1976d2;
        }

        .contact{
            p{
                font-size: 15px;
                color: #424242;
            }
            .btn-send{
                background-color: transparent;
                border: 0;
                font-size: 24px;
            }
        }
    }
    .copyright{
        border: none;
        background-color: #ededed;
        padding-top: 15px;
        color: #424242;
    }
</style>
