const toRegis = document.getElementById('toRegis');
const toLogin = document.getElementById('toLogin');
// Toggle Forms
if (toRegis && toLogin) {
    toRegis.addEventListener('click', () => toggleForms(true));
    toLogin.addEventListener('click', () => toggleForms(false));
}

function toggleForms(isRegister) {
    const forms = document.getElementById('forms');
    forms.style.transform = isRegister ? 'translateX(-50%)' : 'translateX(0)';
}
window.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    if(params.get('isLogin')==='false') {
        toggleForms(true);
    }
    else {
        toggleForms(false);
    }
});