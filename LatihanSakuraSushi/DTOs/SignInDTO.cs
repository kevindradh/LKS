using System.ComponentModel;

namespace LatihanSakuraSushi.DTOs
{
    public class SignInDTO
    {
        [DefaultValue("darmell2")]
        public string Username { get; set; }
        [DefaultValue("Chef123!")]
        public string Password { get; set; }
    }
}
