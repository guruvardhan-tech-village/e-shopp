const containerEl = document.querySelector(".container");
const carrers = ["Web Developer", "IT Designer", "Freelancer", "Content Creator","Architecture Designer", "UI/UX Designer", "Graphic Designer", "Game Developer", "Mobile App Developer", "Data Scientist",
    "Electrical Engineer", "Mechanical Engineer", "Civil Engineer", "Chemical Engineer", "Aerospace Engineer", "Biomedical Engineer", "Environmental Engineer", "Software Engineer", "Network Engineer", 
    "Database Administrator","Digital Marketer", "Social Media Manager", "SEO Specialist", "Content Strategist", "Copywriter", "Brand Manager", "Public Relations Specialist","Financial Analyst", "Investment Banker", "Accountant", "Auditor", "Financial Planner", "Risk Manager","Teacher", "Professor", "Researcher", "Librarian","Doctor", "Nurse", "Pharmacist","Lawyer", "Judge","Chef","Artist","Musician","Writer","Actor"];

let carrersIndex = 0;
let charIndex = 0;
function updateText() {
    
    containerEl.innerHTML = `<h1> I am  ${carrers[carrersIndex].slice(0,1)=== "I" || carrers[carrersIndex].slice(0,1)=== "U" || carrers[carrersIndex].slice(0,1)=== "A"|| carrers[carrersIndex].slice(0,1)=== "O" || carrers[carrersIndex].slice(0,1)=== "E" ? "an" : "a"} ${carrers[carrersIndex].slice(0, charIndex)} </h1>`;
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