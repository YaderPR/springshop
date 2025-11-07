import { motion } from "framer-motion";
import { Check } from "lucide-react";

// Variantes para el círculo exterior
const circleVariants = {
  hidden: { scale: 0 },
  visible: { 
    scale: 1,
    transition: {
      type: "spring",
      stiffness: 260,
      damping: 20,
      delay: 0.1,
    }
  },
};

// Variantes para el icono del check
const checkVariants = {
  hidden: { pathLength: 0, opacity: 0 },
  visible: { 
    pathLength: 1, 
    opacity: 1,
    transition: { 
      duration: 0.4,
      ease: "easeInOut",
      delay: 0.3 
    }
  },
};

export default function AnimatedCheck() {
  return (
    <motion.div
      className="w-24 h-24 rounded-full border-4 border-secondary flex items-center justify-center bg-secondary/10"
      variants={circleVariants}
      initial="hidden"
      animate="visible"
    >
      <motion.svg
        xmlns="http://www.w3.org/2000/svg"
        width="64"
        height="64"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="3"
        strokeLinecap="round"
        strokeLinejoin="round"
        className="text-secondary"
      >
        <motion.path
          d="M20 6L9 17L4 12"
          variants={checkVariants}
        />
      </motion.svg>
    </motion.div>
  );
}