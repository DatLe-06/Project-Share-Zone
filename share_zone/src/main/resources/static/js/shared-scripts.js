let zone_add_media = document.querySelector(".zone-add-media");
let model_overlay = document.querySelector(".model-overlay");
let dropdownMenu = document.querySelector(".dropdownMenu");
let sign_in = document.querySelector("#sign-in-popup");
let sign_up = document.querySelector("#sign-up-popup");
let uploaded = document.querySelector("#uploaded");
let favourite = document.querySelector("#favourite");
let target = document.querySelector("#target");

let signInEmail = document.querySelector("#signInEmail");
let signUpName = document.querySelector("#signUpName");
let signUpEmail = document.querySelector("#signUpEmail");

function start() {
    if (signInEmail.value !== "") openSignInPopup();
    if (signUpName.value !== "" || signUpEmail.value !== "") openSignUpPopup();
    if (target.value === "uploaded") openMediaUploaded();
    else openMediaFavourite();

    balanceColumns();
    adjustColumnCount();
}

function toggleDropdown() {
    const menu = document.getElementById("dropdownMenu");
    menu.style.display = (menu.style.display === "block") ? "none" : "block";
}

function openZoneAddMedia() {
    zone_add_media.classList.add("visible");
    model_overlay.classList.add("visible");
}

function openSignInPopup() {
    if (sign_up !== null) closeSignUpPopup();
    if (sign_in !== null) sign_in.classList.add("visible");
    model_overlay.classList.add("visible");

    if (signInEmail !== null) document.querySelector("#email").value = signInEmail.value;
}

function closeSignInPopup() {
    if (sign_in !== null) sign_in.classList.remove("visible");
    model_overlay.classList.remove("visible");
}

function openSignUpPopup() {
    if (sign_in !== null) closeSignInPopup();
    if (sign_up !== null) sign_up.classList.add("visible");
    model_overlay.classList.add("visible");

    if (signUpName !== null) document.querySelector("#name").value = signUpName.value;
}

function closeSignUpPopup() {
    if (sign_up !== null) sign_up.classList.remove("visible");
    model_overlay.classList.remove("visible");
}

function openMediaUploaded() {
    if (uploaded !== null) uploaded.classList.add("active");
    if (favourite !== null) favourite.classList.remove("active")
}

function openMediaFavourite() {
    if (favourite !== null) favourite.classList.add("active");
    if (uploaded !== null) uploaded.classList.remove("active")
}

model_overlay.addEventListener("click", function () {
    if (zone_add_media !== null) zone_add_media.classList.remove("visible");
    if (sign_in !== null) sign_in.classList.remove("visible");
    if (sign_up !== null) sign_up.classList.remove("visible");
    model_overlay.classList.remove("visible");
})

const tabs = document.querySelectorAll('.tab');
tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        tabs.forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
    });
});

//comment time
function timeDiffDisplay(timestampStr) {
    const timestamp = new Date(timestampStr);
    const now = new Date();

    // So sánh theo ngày (bỏ qua giờ phút giây)
    const tsDateOnly = new Date(timestamp.getFullYear(), timestamp.getMonth(), timestamp.getDate());
    const nowDateOnly = new Date(now.getFullYear(), now.getMonth(), now.getDate());

    const msPerDay = 1000 * 60 * 60 * 24;
    const diffMs = nowDateOnly - tsDateOnly;
    const daysDiff = diffMs / msPerDay;

    if (daysDiff === 0) {
        // Trong ngày → tính giờ
        const hourDiff = (now - timestamp) / (1000 * 60 * 60);
        const roundedHour = Math.round(hourDiff);
        return `${roundedHour} giờ trước`;
    } else {
        // Khác ngày → tính ngày
        const roundedDays = Math.round(daysDiff);
        return `${roundedDays} ngày trước`;
    }
}


//preview avatar
const inputAvatar = document.getElementById('avatar-input');
const previewAvatar = document.getElementById('avatar-preview');

inputAvatar.addEventListener('change', function () {
    const file = this.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            previewAvatar.setAttribute('src', e.target.result);
        }
        reader.readAsDataURL(file);
    }
});

// Cân bằng các cột để giảm khoảng trống
function balanceColumns() {
    const gridContainer = document.querySelector('.grid-container');
    if (!gridContainer) return;

    const gridItems = Array.from(document.querySelectorAll('.grid-item'));
    if (gridItems.length === 0) return;

    // Lấy số cột hiện tại
    const columnCount = parseInt(getComputedStyle(gridContainer).columnCount) || 4;
    if (columnCount === 1) return; // Không cần cân bằng nếu chỉ có 1 cột

    // Tạo mảng để theo dõi chiều cao mỗi cột
    const columnHeights = new Array(columnCount).fill(0);
    const columns = new Array(columnCount).fill().map(() => []);

    // Tính chiều cao mỗi grid-item và phân bổ vào cột
    gridItems.forEach(item => {
        // Tính chiều cao (bao gồm margin-bottom)
        const height = item.getBoundingClientRect().height + 20; // 20px từ margin-bottom
        // Tìm cột có tổng chiều cao nhỏ nhất
        const minHeightIndex = columnHeights.indexOf(Math.min(...columnHeights));
        // Thêm item vào cột đó
        columns[minHeightIndex].push(item);
        // Cập nhật chiều cao cột
        columnHeights[minHeightIndex] += height;
    });

    // Tạo container mới để sắp xếp lại
    const tempContainer = document.createElement('div');
    tempContainer.style.display = 'flex';
    tempContainer.style.flexWrap = 'wrap';
    tempContainer.style.columnGap = '20px';

    // Tạo các cột
    for (let i = 0; i < columnCount; i++) {
        const column = document.createElement('div');
        column.style.flex = '1';
        column.style.display = 'flex';
        column.style.flexDirection = 'column';
        column.style.gap = '20px';
        columns[i].forEach(item => {
            column.appendChild(item);
        });
        tempContainer.appendChild(column);
    }

    // Thay thế nội dung grid-container
    gridContainer.innerHTML = '';
    gridContainer.style.columnCount = '';
    gridContainer.style.display = 'flex';
    gridContainer.appendChild(tempContainer);
}

// Điều chỉnh số cột động dựa trên kích thước màn hình
function adjustColumnCount() {
    const gridContainer = document.querySelector('.grid-container');
    if (!gridContainer) return;

    function updateColumns() {
        const width = window.innerWidth;
        let columnCount;
        if (width <= 480) {
            columnCount = 1;
        } else if (width <= 768) {
            columnCount = 2;
        } else if (width <= 992) {
            columnCount = 3;
        } else {
            columnCount = 4;
        }
        // Chỉ áp dụng column-count nếu không dùng balanceColumns
        if (getComputedStyle(gridContainer).display !== 'flex') {
            gridContainer.style.columnCount = columnCount;
        }
    }

    updateColumns();
    window.addEventListener('resize', () => {
        updateColumns();
        balanceColumns();
    });
}