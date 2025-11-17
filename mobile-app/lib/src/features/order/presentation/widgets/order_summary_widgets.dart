import 'package:flutter/material.dart';
import 'package:springshop/src/features/cart/domain/entities/cart_item.dart';
// Importamos las clases de la pantalla principal (incluyendo OrderSummaryCalculated)
import '../screens/order_summary_screen.dart';

// ====================================================================
// WIDGETS MODULARES
// ====================================================================

/// Título de sección reusable.
class SectionTitle extends StatelessWidget {
  final String title;
  const SectionTitle({super.key, required this.title});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Text(
        title,
        style: textTheme.titleLarge!.copyWith(
          fontWeight: FontWeight.bold,
          color: colorScheme.onSurface,
        ),
      ),
    );
  }
}

/// Muestra los IDs de la orden (solo para demostración/debug).
class OrderIdsDebugInfo extends StatelessWidget {
  final int cartId;
  final int userId;
  final int addressId;

  const OrderIdsDebugInfo({
    super.key,
    required this.cartId,
    required this.userId,
    required this.addressId,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: colorScheme.secondaryContainer,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: colorScheme.outlineVariant),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'IDs para la Solicitud de Pago',
            style: textTheme.bodyMedium!.copyWith(
              fontWeight: FontWeight.w600,
              color: colorScheme.onSecondaryContainer,
            ),
          ),
          const Divider(height: 10),
          Text('Cart ID: $cartId'),
          Text('User ID: $userId'),
          Text('Address ID: $addressId'),
        ],
      ),
    );
  }
}

/// Muestra la información de la dirección de envío.
class ShippingAddressCard extends StatelessWidget {
  final String addressLine1;
  final String? username;
  final String city;
  final String zipCode;
  final String? phoneNumber; // 🚨 ¡Añadido el nuevo parámetro!

  const ShippingAddressCard({
    super.key,
    this.username,
    required this.addressLine1,
    required this.city,
    required this.zipCode,
    this.phoneNumber, // Hacemos el teléfono opcional
  });

  // Widget auxiliar para mostrar una línea de información
  Widget _buildInfoRow(BuildContext context, IconData icon, String text) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 4.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, size: 20, color: Theme.of(context).colorScheme.primary),
          const SizedBox(width: 12),
          Expanded(
            child: Text(text, style: Theme.of(context).textTheme.bodyLarge),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              "Destinatario ($username)", // Nombre hardcodeado para el ejemplo
              style: textTheme.titleMedium!.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),

            // 1. Dirección (Calle y Estado/País)
            _buildInfoRow(context, Icons.home_outlined, addressLine1),

            // 2. Ciudad y Zip
            _buildInfoRow(
              context,
              Icons.location_city_outlined,
              '$city, $zipCode',
            ),

            // 3. Teléfono (Condicional)
            if (phoneNumber != null && phoneNumber!.isNotEmpty)
              _buildInfoRow(context, Icons.phone_outlined, phoneNumber!),

            const SizedBox(height: 8),
            TextButton.icon(
              onPressed: () {
                // Lógica futura para navegar a la selección de dirección
                print('Navegar a la selección/edición de dirección...');
              },
              icon: Icon(Icons.edit, size: 18, color: colorScheme.secondary),
              label: Text(
                'Cambiar',
                style: TextStyle(color: colorScheme.secondary),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Muestra la lista de artículos en el pedido.
class OrderItemsList extends StatelessWidget {
  // Nota: CartItem está definida en order_summary_screen.dart
  final List<CartItem> items;

  const OrderItemsList({super.key, required this.items});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Column(
      children: items.map((item) {
        // Utilizamos el productDetails real, si existe.
        final product = item.productDetails;

        if (product == null) {
          return const Text('Error: Producto no disponible');
        }

        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 8.0),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Container(
                width: 50,
                height: 50,
                decoration: BoxDecoration(
                  color: colorScheme.surfaceVariant,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(
                  Icons.shopping_bag_outlined,
                  color: colorScheme.primary,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      product.name,
                      style: textTheme.titleSmall!.copyWith(
                        fontWeight: FontWeight.w500,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    Text(
                      'Cantidad: ${item.quantity}',
                      style: textTheme.bodySmall!.copyWith(
                        color: colorScheme.outline,
                      ),
                    ),
                  ],
                ),
              ),
              Text(
                ' \$${item.subtotal.toStringAsFixed(2)}',
                style: textTheme.titleMedium!.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }
}

/// Fila individual para el detalle del precio (Subtotal, Envío, Impuestos, Total).
class _PriceRow extends StatelessWidget {
  final String label;
  final double amount;
  final bool isTotal;

  const _PriceRow({
    required this.label,
    required this.amount,
    this.isTotal = false,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            label,
            style: textTheme.bodyLarge!.copyWith(
              fontSize: isTotal ? 18 : 15,
              fontWeight: isTotal ? FontWeight.bold : FontWeight.normal,
              color: isTotal
                  ? colorScheme.onSurface
                  : colorScheme.onSurfaceVariant,
            ),
          ),
          Text(
            '\$${amount.toStringAsFixed(2)}',
            style: textTheme.bodyLarge!.copyWith(
              fontSize: isTotal ? 18 : 15,
              fontWeight: isTotal ? FontWeight.bold : FontWeight.w500,
              color: isTotal ? colorScheme.primary : colorScheme.onSurface,
            ),
          ),
        ],
      ),
    );
  }
}

/// Muestra el resumen de precios y el total a pagar.
class PriceSummaryCard extends StatelessWidget {
  // Nota: OrderSummaryCalculated está definida en order_summary_screen.dart
  final OrderSummaryCalculated summary;

  const PriceSummaryCard({super.key, required this.summary});

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 1,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            _PriceRow(label: 'Subtotal', amount: summary.subtotal),
            _PriceRow(label: 'Costo de Envío', amount: summary.shippingCost),
            _PriceRow(
              label: 'Impuestos (${(summary.taxRate * 100).toInt()}%)',
              amount: summary.taxes,
            ),
            const Divider(height: 20),
            _PriceRow(
              label: 'Total a Pagar',
              amount: summary.total,
              isTotal: true,
            ),
          ],
        ),
      ),
    );
  }
}

/// Botón fijo en la parte inferior para proceder al pago.
class PayNowButton extends StatelessWidget {
  const PayNowButton({super.key});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 10.0),
      decoration: BoxDecoration(
        color: colorScheme.surface,
        boxShadow: [
          BoxShadow(
            color: colorScheme.shadow.withOpacity(0.15),
            blurRadius: 5.0,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: ElevatedButton(
        onPressed: () {
          // Lógica de pago
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('FUNCIONALIDAD PENDIENTE: Llamada a OrderService.'),
              duration: Duration(seconds: 2),
            ),
          );
        },
        style: ElevatedButton.styleFrom(
          minimumSize: const Size(double.infinity, 50),
          backgroundColor: colorScheme.primary,
          foregroundColor: colorScheme.onPrimary,
          textStyle: const TextStyle(fontWeight: FontWeight.bold),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          elevation: 0,
        ),
        child: const Text('Pagar Ahora', style: TextStyle(fontSize: 18)),
      ),
    );
  }
}
