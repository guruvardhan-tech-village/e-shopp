const containerEl = document.querySelector(".container");
const carrers = ["Web Developer", "IT Designer", "Freelancer", "Content Creator","Architecture Designer", "UI/UX Designer", "Graphic Designer", "Game Developer", "Mobile App Developer", "Data Scientist"];

let carrersIndex = 0;
let charIndex = 0;
function updateText() {
    
    containerEl.innerHTML = `<h1> I am  ${carrers[carrersIndex].slice(0,1)=== "I" || carrers[carrersIndex].slice(0,1)=== "U" || carrers[carrersIndex].slice(0,1)=== "A" ? "an" : "a"} ${carrers[carrersIndex].slice(0, charIndex)} </h1>`;
    charIndex++;
    setTimeout(updateText, 150);
    if(charIndex === carrers[carrersIndex].length + 1) {
        charIndex = 0;
        carrersIndex++;
    }
    if(carrersIndex === carrers.length) {
        carrersIndex = 0;
    }
}
updateText();