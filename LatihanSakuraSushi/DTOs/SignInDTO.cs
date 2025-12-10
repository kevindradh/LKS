using System.ComponentModel;

namespace LatihanSakuraSushi.DTOs
{
    public class SignInDTO
    {
        [DefaultValue("fcerith0")]
        public string Username { get; set; }
        [DefaultValue("Cashier123!")]
        public string Password { get; set; }
    }
}
