
        const buttonContainer = document.querySelector('.button-container');
        buttonContainer.addEventListener('click', (e) => {
            if(e.target.classList.contains('hellobtn')){
                alert('Hello');
            }
            if(e.target.classList.contains('addtocartbtn')){
                alert('Added to Cart');
            }
            if(e.target.classList.contains('removefromcartbtn')){
                alert('Removed from Cart');
            }
        });