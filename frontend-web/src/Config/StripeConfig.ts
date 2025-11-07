// src/config/stripeConfig.ts
import { loadStripe } from "@stripe/stripe-js";

export const stripePromise = loadStripe(import.meta.env.REACT_STRIPE_PUBLIC_KEY!);
