/* 校园二手交易与失物招领管理系统 前端脚本 */
(function () {
    "use strict";

    // 统一的删除 / 危险操作二次确认：为元素添加 data-confirm="提示语"
    document.addEventListener("click", function (e) {
        var el = e.target.closest("[data-confirm]");
        if (el) {
            if (!window.confirm(el.getAttribute("data-confirm"))) {
                e.preventDefault();
            }
        }
    });

    // 图片选择后即时预览：<input type="file" data-preview="预览元素id">
    document.addEventListener("change", function (e) {
        var input = e.target;
        if (input.matches('input[type=file][data-preview]')) {
            var target = document.getElementById(input.getAttribute("data-preview"));
            if (target && input.files && input.files[0]) {
                var reader = new FileReader();
                reader.onload = function (ev) {
                    target.innerHTML = '<img src="' + ev.target.result + '" alt="预览">';
                };
                reader.readAsDataURL(input.files[0]);
            }
        }
    });

    // 几秒后自动隐藏成功提示
    window.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll(".alert-success").forEach(function (box) {
            setTimeout(function () {
                box.style.transition = "opacity .5s";
                box.style.opacity = "0";
                setTimeout(function () { box.style.display = "none"; }, 500);
            }, 3000);
        });
    });
})();
