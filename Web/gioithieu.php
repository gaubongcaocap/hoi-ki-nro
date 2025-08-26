<?php
include 'Controllers/Header.php';
?>

<?php if ($_Login != null) { ?>
<div class="card">
    <div class="card-body">

       

        <!-- Phần văn bản để copy -->
        <div class="text-copy" style="margin: 20px 0; padding: 15px; border: 1px solid #ccc; background-color: #f9f9f9;">
            <h3 style="color: #2c3e50; margin-bottom: 10px;">Copy đoạn văn bản dưới đây gửi cho bạn bè, facebook,các box zalo ngọc rồng, giới thiệu người chơi mới đăng kí cho bạn, bạn sẽ nhận được điểm khi có người chơi đăng kí bằng link bạn gửi, lưu ý người đã đăng kí rồi khi đăng kí lại sẽ không được cộng điểm:</h3>
            
            <textarea style="width: 100%; height: 100px; padding: 10px; font-family: Arial, sans-serif;" readonly>
Link giới thiệu: http://nromubi.com/auth/Register?idRef=<?php echo $_Id; ?>
				
Nro test cực hấp dẫn, 2 bản tf, chơi là nghẹo
            </textarea>
        </div>

    </div>
</div>
<?php } else { ?>
    <center><h2>Bạn phải đăng nhập mới làm được sự kiện</h2></center>
<?php } ?>

<?php include 'Controllers/Footer.php'; ?>
