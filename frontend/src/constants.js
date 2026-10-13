// Fixed lists used in the forms
export const GENDERS = ["Female", "Male"];
export const CATEGORIES = ["General", "OBC", "BC", "SC", "ST", "EBC", "Minority"];
export const LEVELS = ["Diploma", "UG", "PG", "PhD", "Any"];

// "All" or a list like "SC,ST" -> readable text
export function eligibilityText(scholarship) {
  const gender = scholarship.gender === "All" ? "All genders" : scholarship.gender + " only";
  const categories = scholarship.categories === "All" ? "all categories" : scholarship.categories.split(",").join(", ");
  return gender + " · " + categories;
}

export function isClosed(scholarship) {
  return scholarship.deadline < new Date().toISOString().slice(0, 10);
}
