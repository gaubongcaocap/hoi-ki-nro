<?php
include '../../Controllers/Header.php';
?>

    
        
<div class="card">
    <div class="card-body">
        <div class="row">

            <!-- Tải game cho IOS -->
			
			
			<div class="col-md-4 mt-1">
                <div class="card download-bg">
                    <div class="card-body text-center">
                        <h5 class="card-title">Tải game cho IOS TF</h5>
						<p class="card-text">Hiện tại chưa có bản TF các bạn dùng tạm file IPA nha ♥ </p>
                        <a href="../download/NroLegion.ipa" class="btn btn-danger me-1 mt-1">Tải Ngay</a>
                    </div>
                </div>
            </div>
			
            <div class="col-md-4 mt-1">
                <div class="card download-bg">
                    <div class="card-body text-center">
                        <h5 class="card-title">Tải game cho IOS Iphone</h5>
                        <p class="card-text">Tải File Ipa - Cài ứng dụng E-sign và cài nha.</p>
                        <a href="../download/NroLegion.ipa" class="btn btn-danger me-1 mt-1">Tải Ngay</a>
                    </div>
                </div>
            </div>

            <!-- Tải game cho Android -->
            <div class="col-md-4 mt-1">
                <div class="card download-bg suggestion">
                    <div class="card-body text-center">
                        <h5 class="card-title">Tải game cho Android</h5>
                        <p class="card-text">Nhấn vào đây để tải game cho điện thoại Android của bạn.</p>
                        <a href="../download/NroLegion.apk" class="btn btn-danger me-1 mt-1">Tải Ngay</a>
                    </div>
                </div>
            </div>

            <!-- Tải game cho PC -->
            <div class="col-md-4 mt-1">
                <div class="card download-bg">
                    <div class="card-body text-center">
                        <h5 class="card-title">Tải game cho PC Window</h5>
                        <p class="card-text">Nhấn vào đây để tải game cho máy tính để bàn của bạn.</p>
                        <a href="../download/NroLegion.rar" class="btn btn-danger me-1 mt-1">Tải Ngay</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function () {

        $('[data-bs-toggle="modal"]').click(function (e) {
            e.preventDefault(); // Ngăn chặn liên kết mở trang web khác

            var targetModal = $(this).attr('data-bs-target');

            $(targetModal).modal('show');
        });

        $('.modal .btn-close').click(function () {

            $(this).closest('.modal').modal('hide');
        });
    });
</script>
<?php
include '../../Controllers/Footer.php';
?>