<?php
include '../../Controllers/Header.php';
?>

<div class="container px-0">
    <div class="card">
        <div class="card-body">
            <h5 class="mt-0 mb-20">Lịch sử nạp thẻ</h5>
            <div id="napthe-history" class="table-responsive">
                <!-- Dữ liệu lịch sử nạp thẻ sẽ được cập nhật bằng AJAX -->
            </div>

            <h5 class="mt-30 mb-20">Lịch sử giao dịch ATM</h5>
            <div id="atm-lichsu" class="table-responsive">
                <!-- Dữ liệu lịch sử giao dịch ATM sẽ được cập nhật bằng AJAX -->
            </div>
        </div>
    </div>
</div>

<script>
    // Hàm gửi yêu cầu AJAX để tải dữ liệu lịch sử nạp thẻ
    function loadNapTheHistory(page) {
        $.ajax({
            url: '/Api/CardHistory',
            type: 'GET',
            data: { page: page },
            success: function (response) {
                $('#napthe-history').html(response);
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }

    // Hàm gửi yêu cầu AJAX để tải dữ liệu lịch sử giao dịch ATM
    function loadATMLichsu(page) {
        $.ajax({
            url: '/Api/AtmHistory',
            type: 'GET',
            data: { page: page },
            success: function (response) {
                $('#atm-lichsu').html(response);
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }

    $(document).ready(function () {
        // Load trang đầu tiên của lịch sử nạp thẻ và lịch sử giao dịch ATM khi trang được tải
        loadNapTheHistory(1);
        loadATMLichsu(1);
    });
   // Hàm gửi yêu cầu AJAX khi người dùng nhấp vào liên kết phân trang
   $(document).on('click', '.pagination-custom-style a', function (e) {
        e.preventDefault();
        var page = $(this).attr('href').split('page=')[1];
        loadNapTheHistory(page);
    });
    // Hàm gửi yêu cầu AJAX khi người dùng nhấp vào liên kết phân trang
   $(document).on('click', '.pagination-custom-style2 a', function (e) {
        e.preventDefault();
        var page = $(this).attr('href').split('page=')[1];
        loadATMLichsu(page);
    });
</script>

<?php
include '../../Controllers/Footer.php';
?>