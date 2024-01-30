let openShopping = document.querySelector('.shopping');
let closeShopping = document.querySelector('.closeShopping');
let list = document.querySelector('.list');
let list1 = document.querySelector('.list1');
let listCard = document.querySelector('.listCard');
let body = document.querySelector('body');
let total = document.querySelector('.total');
let quantity = document.querySelector('.quantity');
const scrollContainer = document.querySelector(".scroll-container");
// Get the modal element
var modal = document.getElementById("orderModal");

// Get the button that opens the modal
var orderButton = document.querySelector(".order-button");

// Get the <span> element that closes the modal
var span = document.querySelector(".close");

openShopping.addEventListener('click', () => {
    body.classList.add('active');
})
closeShopping.addEventListener('click', () => {
    body.classList.remove('active');
})
scrollContainer.addEventListener("wheel", (e) => {
    if (e.deltaY > 0) {
        scrollContainer.scrollTop += 100;
    } else {
        scrollContainer.scrollTop -= 100;
    }
    e.preventDefault();
});

// When the user clicks the "Order" button, open the modal
orderButton.addEventListener("click", openModal);

// When the user clicks on <span> (x), close the modal
span.addEventListener("click", closeModal);

// When the user clicks anywhere outside the modal, close it
window.addEventListener("click", function (event) {
    if (event.target === modal) {
        closeModal();
    }
});
let products = [
    {
        id: 1,
        name: 'PRODUCT NAME 1',
        image: '1.PNG',
        price: 120000
    },
    {
        id: 2,
        name: 'PRODUCT NAME 2',
        image: '2.PNG',
        price: 120000
    },
    {
        id: 3,
        name: 'PRODUCT NAME 3',
        image: '3.PNG',
        price: 220000
    },
    {
        id: 4,
        name: 'PRODUCT NAME 4',
        image: '4.PNG',
        price: 123000
    },
    {
        id: 5,
        name: 'PRODUCT NAME 5',
        image: '5.PNG',
        price: 320000
    },
    {
        id: 6,
        name: 'PRODUCT NAME 6',
        image: '6.PNG',
        price: 120000
    },
    {
        id: 7,
        name: 'PRODUCT NAME 7',
        image: 'bg_1.png',
        price: 120000
    },
    {
        id: 8,
        name: 'PRODUCT NAME 8',
        image: 'bg_2.png',
        price: 120000
    },
    {
        id: 9,
        name: 'PRODUCT NAME 9',
        image: 'menu-item-1.png',
        price: 220000
    },
    {
        id: 10,
        name: 'PRODUCT NAME 10',
        image: 'menu-item-2.png',
        price: 123000
    },
    {
        id: 11,
        name: 'PRODUCT NAME 11',
        image: 'menu-item-3.png',
        price: 320000
    },
    {
        id: 12,
        name: 'PRODUCT NAME 12',
        image: 'menu-item-4.png',
        price: 120000
    },
    {
        id: 13,
        name: 'PRODUCT NAME 13',
        image: 'menu-item-5.png',
        price: 120000
    },
    {
        id: 14,
        name: 'PRODUCT NAME 14',
        image: 'menu-item-6.png',
        price: 120000
    },
    {
        id: 15,
        name: 'PRODUCT NAME 15',
        image: 'pasta-1.jpg',
        price: 120000
    },
    {
        id: 16,
        name: 'PRODUCT NAME 1',
        image: 'drink-1.jpg',
        price: 120000
    },
    {
        id: 17,
        name: 'PRODUCT NAME 2',
        image: 'drink-2.jpg',
        price: 120000
    },
    {
        id: 18,
        name: 'PRODUCT NAME 3',
        image: 'drink-3.jpg',
        price: 220000
    },
    {
        id: 19,
        name: 'PRODUCT NAME 4',
        image: 'drink-4.jpg',
        price: 123000
    },
    {
        id: 20,
        name: 'PRODUCT NAME 5',
        image: 'drink-5.jpg',
        price: 320000
    },
    {
        id: 21,
        name: 'PRODUCT NAME 6',
        image: 'drink-6.jpg',
        price: 120000
    },
    {
        id: 22,
        name: 'PRODUCT NAME 7',
        image: 'drink-7.jpg',
        price: 120000
    },
    {
        id: 23,
        name: 'PRODUCT NAME 8',
        image: 'drink-8.jpg',
        price: 120000
    },
    {
        id: 24,
        name: 'PRODUCT NAME 9',
        image: 'drink-9.jpg',
        price: 220000
    }
];
let listCards = [];
// Function to open the modal
function openModal() {
    modal.style.display = "block";
}
// Function to close the modal
function closeModal() {
    modal.style.display = "none";
}
function initApp() {
    products.forEach((value, key) => {
        if (key < 15) {
            let newDiv = document.createElement('div');
            newDiv.classList.add('item');
            newDiv.innerHTML = `
            <img src="../Images/${value.image}">
            <div class="title">${value.name}</div>
            <div class="price">${value.price.toLocaleString()}</div>
            <button onclick="addToCard(${key})">Add To Card</button>`;
            list.appendChild(newDiv);
        } else {
            let newDiv = document.createElement('div');
            newDiv.classList.add('item');
            newDiv.innerHTML = `
                <img src="../Images/${value.image}">
                <div class="title">${value.name}</div>
                <div class="price">${value.price.toLocaleString()}</div>
                <button onclick="addToCard(${key})">Add To Card</button>`;
            list1.appendChild(newDiv);
        }
    })

}
initApp();
function addToCard(key) {
    if (listCards[key] == null) {
        // copy product form list to list card
        listCards[key] = JSON.parse(JSON.stringify(products[key]));
        listCards[key].quantity = 1;
    }
    reloadCard();
}
function reloadCard() {
    listCard.innerHTML = '';
    let count = 0;
    let totalPrice = 0;
    listCards.forEach((value, key) => {
        totalPrice = totalPrice + value.price;
        count = count + value.quantity;
        if (value != null) {
            let newDiv = document.createElement('li');
            newDiv.innerHTML = `
                <div><img src="../Images/${value.image}"/></div>
                <div>${value.name}</div>
                <div>${value.price.toLocaleString()}</div>
                <div>
                    <button onclick="changeQuantity(${key}, ${value.quantity - 1})">-</button>
                    <div class="count">${value.quantity}</div>
                    <button onclick="changeQuantity(${key}, ${value.quantity + 1})">+</button>
                </div>`;
            listCard.appendChild(newDiv);
        }
    })
    total.innerText = totalPrice.toLocaleString();
    quantity.innerText = count;
}
function changeQuantity(key, quantity) {
    if (quantity == 0) {
        delete listCards[key];
    } else {
        listCards[key].quantity = quantity;
        listCards[key].price = quantity * products[key].price;
    }
    reloadCard();
}

function clearCart() {
    // Clear the listCards array
    listCards = [];
    // Clear the listCard UI
    listCard.innerHTML = '';
    // Reset the total and quantity to 0
    total.innerText = '0';
    quantity.innerText = '0';
}