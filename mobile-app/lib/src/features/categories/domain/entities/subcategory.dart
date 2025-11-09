// --- Entidad Mock ---
class Subcategory {
  final String id;
  final String name;
  final String imageUrl;
  final List<int> ids; // IDs mock para la navegación

  const Subcategory({
    required this.id,
    required this.name, 
    required this.imageUrl,
    required this.ids,
  });
}