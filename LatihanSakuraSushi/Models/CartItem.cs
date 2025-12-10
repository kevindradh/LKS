using System;
using System.Collections.Generic;

namespace LatihanSakuraSushi.Models;

public partial class CartItem
{
    public Guid Id { get; set; }

    public Guid TransactionId { get; set; }

    public Guid ItemId { get; set; }

    public int Quantity { get; set; }

    public decimal Price { get; set; }

    public decimal TotalPrice { get; set; }

    public DateTimeOffset AddedAt { get; set; }

    public virtual Item Item { get; set; } = null!;

    public virtual Transaction Transaction { get; set; } = null!;
}
