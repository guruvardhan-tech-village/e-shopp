const containerEl = document.querySelector(".container");
const careers = ["Web Developer", "IT Designer", "Freelancer", "Content Creator","Architecture Designer", "UI/UX Designer", "Graphic Designer", "Game Developer", "Mobile App Developer", "Data Scientist",
    "Electrical Engineer", "Mechanical Engineer", "Civil Engineer", "Chemical Engineer", "Aerospace Engineer", "Biomedical Engineer", "Environmental Engineer", "Software Engineer", "Network Engineer", 
    "Database Administrator","Digital Marketer", "Social Media Manager", "SEO Specialist", "Content Strategist", "Copywriter", "Brand Manager", "Public Relations Specialist","Financial Analyst", "Investment Banker", "Accountant", "Auditor", "Financial Planner", "Risk Manager","Teacher", "Professor", "Researcher", "Librarian","Doctor", "Nurse", "Pharmacist","Lawyer", "Judge","Chef","Artist","Musician","Writer","Actor"];

let careersIndex = 0;
let charIndex = 0;
function updateText() {
    
    containerEl.innerHTML = `<h1> I am  ${careers[careersIndex].slice(0,1)=== "I" || careers[careersIndex].slice(0,1)=== "U" || careers[careersIndex].slice(0,1)=== "A"|| careers[careersIndex].slice(0,1)=== "O" || careers[careersIndex].slice(0,1)=== "E" ? "an" : "a"} ${careers[careersIndex].slice(0, charIndex)} </h1>`;
    charIndex++;
    setTimeout(updateText, 150);
    if(charIndex === careers[careersIndex].length + 1) {
        charIndex = 0;
        careersIndex++;
    }
    if(careersIndex === careers.length) {
        careersIndex = 0;
    }
}
updateText();