import { useEffect, useState } from "react";

const images = [
  "https://images.unsplash.com/photo-1607082349566-187342175e2f",
  "https://images.unsplash.com/photo-1581291518633-83b4ebd1d83e",
  "https://images.unsplash.com/photo-1593642634367-d91a135587b5"
];

function HeroSlider() {
  const [index, setIndex] = useState(0);

  // 🔥 AUTO SLIDE
  useEffect(() => {
    const interval = setInterval(() => {
      setIndex((prev) => (prev + 1) % images.length);
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="relative w-full h-72 overflow-hidden">

      {/* SLIDES */}
      {images.map((img, i) => (
        <img
          key={i}
          src={img}
          className={`absolute w-full h-full object-cover transition-opacity duration-700 ${
            i === index ? "opacity-100" : "opacity-0"
          }`}
        />
      ))}

      {/* GRADIENT (Amazon style fade) */}
      <div className="absolute bottom-0 w-full h-20 bg-gradient-to-t from-gray-100 to-transparent"></div>

    </div>
  );
}

export default HeroSlider;