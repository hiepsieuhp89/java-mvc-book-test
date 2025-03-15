// Wait for the DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Add active class to current nav item
    const currentLocation = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
        const linkPath = link.getAttribute('href');
        if (linkPath === currentLocation || 
            (linkPath !== '/' && currentLocation.startsWith(linkPath))) {
            link.classList.add('active');
        }
    });
    
    // Book search form validation
    const searchForm = document.querySelector('form[action="/search"]');
    if (searchForm) {
        searchForm.addEventListener('submit', function(event) {
            const titleInput = document.getElementById('title');
            const categorySelect = document.getElementById('category');
            
            if (!titleInput.value.trim() && !categorySelect.value) {
                event.preventDefault();
                alert('Vui lòng nhập tiêu đề hoặc chọn thể loại để tìm kiếm');
            }
        });
    }
    
    // Registration form validation
    const registrationForm = document.querySelector('form[action="/register"]');
    if (registrationForm) {
        registrationForm.addEventListener('submit', function(event) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password !== confirmPassword) {
                event.preventDefault();
                alert('Mật khẩu và xác nhận mật khẩu không khớp');
            }
        });
    }
    
    // Star rating system for reviews
    const ratingInputs = document.querySelectorAll('.rating-input');
    if (ratingInputs.length > 0) {
        ratingInputs.forEach(input => {
            input.addEventListener('change', function() {
                const stars = document.querySelectorAll('.star-rating label');
                const rating = parseInt(this.value);
                
                stars.forEach((star, index) => {
                    if (index < rating) {
                        star.classList.add('active');
                    } else {
                        star.classList.remove('active');
                    }
                });
            });
        });
    }
    
    // Book checkout confirmation
    const checkoutButtons = document.querySelectorAll('a[href^="/checkout/"]');
    checkoutButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            if (!confirm('Bạn có chắc chắn muốn mượn sách này?')) {
                event.preventDefault();
            }
        });
    });
    
    // Book return confirmation
    const returnButtons = document.querySelectorAll('a[href^="/return/"]');
    returnButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            if (!confirm('Bạn có chắc chắn muốn trả sách này?')) {
                event.preventDefault();
            }
        });
    });
    
    // Admin delete confirmation
    const deleteButtons = document.querySelectorAll('a[href*="/delete/"]');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            if (!confirm('Bạn có chắc chắn muốn xóa mục này? Hành động này không thể hoàn tác.')) {
                event.preventDefault();
            }
        });
    });
    
    // Copy invite link to clipboard
    const copyLinkButton = document.getElementById('copyInviteLink');
    if (copyLinkButton) {
        copyLinkButton.addEventListener('click', function() {
            const inviteLink = document.getElementById('inviteLink');
            inviteLink.select();
            document.execCommand('copy');
            alert('Đã sao chép liên kết mời!');
        });
    }
    
    // Show/hide password toggle
    const passwordToggles = document.querySelectorAll('.password-toggle');
    passwordToggles.forEach(toggle => {
        toggle.addEventListener('click', function() {
            const passwordInput = document.getElementById(this.dataset.target);
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                this.innerHTML = '<i class="fas fa-eye-slash"></i>';
            } else {
                passwordInput.type = 'password';
                this.innerHTML = '<i class="fas fa-eye"></i>';
            }
        });
    });
}); 